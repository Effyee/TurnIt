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

    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponseDto>> getPopularProducts() {
        List<ProductResponseDto> popularProducts = productService.getPopularProducts();
        return ResponseEntity.ok(popularProducts);
    }
    @GetMapping("/hello")
    public String printHello(){
        return "Hello World";
    }
}