package com.turnit.api.service;

import com.turnit.api.dto.ProductResponseDto;
import com.turnit.api.repository.ProductRepository;
import com.turnit.api.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;

    public List<ProductResponseDto> getPopularProducts() {
        Pageable topTen = PageRequest.of(0, 10);
        LocalDateTime since = LocalDateTime.now().minusDays(1); // 최근 24시간
        List<Long> popularProductIds = tradeRepository.findPopularProductIds(since, topTen);
        log.info("Popular product ids: {}", popularProductIds);

        return productRepository.findAllById(popularProductIds).stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }
    public String printHello(){
        return "Hello World!";
    }
}
