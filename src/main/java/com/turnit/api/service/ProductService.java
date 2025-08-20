package com.turnit.api.service;

import com.turnit.api.dto.ProductResponseDto;
import com.turnit.api.repository.ProductRepository;
import com.turnit.api.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;

    /**
     * [캐싱 미적용] DB에서 직접 인기 상품 목록을 조회
     */
    public List<ProductResponseDto> getPopularProductsFromDb() {
        log.info("===== DB에서 직접 인기 상품을 조회합니다. =====");
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        Pageable topTen = PageRequest.of(0, 10);
        List<Long> popularProductIds = tradeRepository.findPopularProductIds(twentyFourHoursAgo, topTen);

        return productRepository.findAllById(popularProductIds).stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * [캐싱 적용] Redis를 통해 인기 상품 목록을 조회.
     * (캐시가 없으면 DB에서 조회 후 Redis에 저장)
     */
    @Cacheable("popular-products")
    public List<ProductResponseDto> getPopularProductsWithCache() {
        log.info("===== (캐시 없음) DB에서 조회하여 캐시에 저장합니다. =====");
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        Pageable topTen = PageRequest.of(0, 10);
        List<Long> popularProductIds = tradeRepository.findPopularProductIds(twentyFourHoursAgo, topTen);

        return productRepository.findAllById(popularProductIds).stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }
}