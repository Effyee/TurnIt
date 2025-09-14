package com.turnit.api.service;

import com.turnit.api.domain.*;
import com.turnit.api.dto.OrderBookResponseDto;
import com.turnit.api.repository.ProductRepository;
import com.turnit.api.repository.SellBidRepository;
import com.turnit.api.repository.TradeRepository;
import com.turnit.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingService {

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SellBidRepository sellBidRepository;

    public void createBid(Long productId, Long price, Long userId, String size, BidType bidType) {
        String key = "product:" + productId + (bidType == BidType.SELL ? ":sell_asks" : ":buy_bids");
        String value = UUID.randomUUID() + ":" + userId + ":" + size; // "입찰ID:유저ID:사이즈"

        redisTemplate.opsForZSet().add(key, value, price);
        log.info("새로운 입찰 등록 완료. Key: {}, Value: {}, Price: {}", key, value, price);
    }

    public OrderBookResponseDto getOrderBook(Long productId) {
        String sellKey = "product:" + productId + ":sell_asks";
        String buyKey = "product:" + productId + ":buy_bids";

        Set<ZSetOperations.TypedTuple<String>> lowestSellTuple = redisTemplate.opsForZSet().rangeWithScores(sellKey, 0, 0);
        Long lowestSellPrice = lowestSellTuple.stream().findFirst().map(tuple -> tuple.getScore().longValue()).orElse(null);

        Set<ZSetOperations.TypedTuple<String>> highestBuyTuple = redisTemplate.opsForZSet().reverseRangeWithScores(buyKey, 0, 0);
        Long highestBuyPrice = highestBuyTuple.stream().findFirst().map(tuple -> tuple.getScore().longValue()).orElse(null);

        return new OrderBookResponseDto(lowestSellPrice, highestBuyPrice);
    }

    @Transactional
    public void executeInstantBuy(Long productId, Long buyerId, Long price) {
        RLock lock = redissonClient.getLock("product:" + productId + ":lock");

        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("락 획득 실패! productId: {}", productId);
                throw new RuntimeException("다른 사용자가 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }
            log.info("락 획득 성공! Thread: {}", Thread.currentThread().getName());

            String sellKey = "product:" + productId + ":sell_asks";
            var lowestSellTuple = redisTemplate.opsForZSet().rangeWithScores(sellKey, 0, 0)
                    .stream().findFirst().orElse(null);

            if (lowestSellTuple == null || lowestSellTuple.getScore() > price) {
                throw new RuntimeException("즉시 구매 가능한 상품이 없거나, 제시한 가격이 너무 낮습니다.");
            }

            redisTemplate.opsForZSet().remove(sellKey, lowestSellTuple.getValue());

            // Redis에 저장된 Value("입찰ID:판매자ID:사이즈")를 파싱합니다.
            String[] valueParts = lowestSellTuple.getValue().split(":");
            Long sellerId = Long.parseLong(valueParts[1]);
            String size = valueParts[2]; // 사이즈 정보 추출

            User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("구매자를 찾을 수 없습니다."));
            User seller = userRepository.findById(sellerId).orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

            // 추출한 정보로 완전한 SellBid 객체를 생성합니다.
            SellBid soldBid = SellBid.builder()
                    .user(seller)
                    .product(product)
                    .price(lowestSellTuple.getScore().longValue())
                    .size(size) // 추출한 사이즈 정보 사용
                    .status(SellBidStatus.SOLD_OUT)
                    .build();
            sellBidRepository.save(soldBid);

            Trade newTrade = Trade.builder()
                    .product(product)
                    .buyer(buyer)
                    .sellBid(soldBid)
                    .price(soldBid.getPrice())
                    .build();
            tradeRepository.save(newTrade);
            log.info("거래 체결 완료! Trade ID: {}", newTrade.getId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락을 기다리는 중 문제가 발생했습니다.", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("락 해제 완료! Thread: {}", Thread.currentThread().getName());
            }
        }
    }
}
