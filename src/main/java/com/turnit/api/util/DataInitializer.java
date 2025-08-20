package com.turnit.api.util;

import com.github.javafaker.Faker;
import com.turnit.api.domain.*;
import com.turnit.api.repository.ProductRepository;
import com.turnit.api.repository.SellBidRepository;
import com.turnit.api.repository.TradeRepository;
import com.turnit.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"prod", "local"})  // local 프로필일 때만 실행되도록 설정
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SellBidRepository sellBidRepository;
    private final TradeRepository tradeRepository;

    @Override
    public void run(String... args) throws Exception {
        // 데이터가 이미 있는지 확인하여 중복 생성을 방지
        if (userRepository.count() > 0) {
            log.info("데이터가 이미 존재하므로 더미 데이터 생성을 건너뜁니다.");
            return;
        }

        log.info("더미 데이터 생성을 시작합니다...");

        Faker faker = new Faker(new Locale("ko"));
        Random random = new Random();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String uniqueEmail = faker.internet().emailAddress().split("@")[0] + i + "@example.com";
            // 닉네임 뒤에도 숫자를 붙여 고유성을 보장합니다.
            String uniqueNickname = faker.name().username() + i;

            users.add(User.builder()
                    .email(uniqueEmail)
                    .password("password123")
                    .nickname(uniqueNickname) // 고유한 닉네임 사용
                    .build());
        }
        userRepository.saveAll(users);
        log.info("유저 50명 생성 완료.");

        // 2. 상품 생성
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            products.add(Product.builder()
                    .name(faker.commerce().productName())
                    .modelNumber(faker.bothify("???-######"))
                    .releasePrice((long) (faker.number().numberBetween(500, 3000) * 100))
                    .imageUrl(faker.internet().avatar())
                    .build());
        }
        productRepository.saveAll(products);
        log.info("상품 100개 생성 완료.");

        // 3. 판매 입찰 생성
        List<SellBid> sellBids = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            sellBids.add(SellBid.builder()
                    .user(users.get(random.nextInt(users.size())))
                    .product(products.get(random.nextInt(products.size())))
                    .size(String.valueOf(faker.number().numberBetween(230, 290)))
                    .price((long) (faker.number().numberBetween(1000, 5000) * 100))
                    .status(SellBidStatus.ON_SALE)
                    .build());
        }
        sellBidRepository.saveAll(sellBids);
        log.info("판매 입찰 300개 생성 완료.");

        // 4. 거래 체결 생성 (인기 상품 조회를 위해 가장 중요!)
        List<Trade> trades = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            SellBid randomBid = sellBids.get(i);
            User buyer;
            do {
                buyer = users.get(random.nextInt(users.size()));
            } while (buyer.getId().equals(randomBid.getUser().getId()));

            trades.add(Trade.builder()
                    .product(randomBid.getProduct())
                    .sellBid(randomBid)
                    .buyer(buyer)
                    .price(randomBid.getPrice())
                    .createdAt(LocalDateTime.now()) // 이 줄을 추가
                    .build());
        }

        tradeRepository.saveAll(trades);
        log.info("거래 200건 생성 완료.");
        log.info("더미 데이터 생성이 완료되었습니다.");
    }
}