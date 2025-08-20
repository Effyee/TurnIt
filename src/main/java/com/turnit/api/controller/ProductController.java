package com.turnit.api.controller;

import com.turnit.api.dto.ProductResponseDto;
import com.turnit.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 캐싱이 적용되지 않은 API
     */
    @GetMapping("/popular/db")
    public ResponseEntity<List<ProductResponseDto>> getPopularProductsFromDb() {
        List<ProductResponseDto> popularProducts = productService.getPopularProductsFromDb();
        return ResponseEntity.ok(popularProducts);
    }

    /**
     * Redis 캐싱이 적용된 API
     */
    @GetMapping("/popular/cache")
    public ResponseEntity<List<ProductResponseDto>> getPopularProductsWithCache() {
        List<ProductResponseDto> popularProducts = productService.getPopularProductsWithCache();
        return ResponseEntity.ok(popularProducts);
    }
}
