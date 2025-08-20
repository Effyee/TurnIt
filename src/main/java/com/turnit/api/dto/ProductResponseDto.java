package com.turnit.api.dto;

import com.turnit.api.domain.Product;

public record ProductResponseDto(
        Long id,
        String name,
        String modelNumber,
        Long releasePrice,
        String imageUrl
) {
    // Product 엔티티를 DTO로 변환하는 정적 팩토리 메소드
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getModelNumber(),
                product.getReleasePrice(),
                product.getImageUrl()
        );
    }
}
