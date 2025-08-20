package com.turnit.api.dto;

public record PopularProductDto(
        Long productId,
        long tradeCount
) {
}