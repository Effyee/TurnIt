package com.turnit.api.dto;

public record OrderBookResponseDto(
        Long lowestSellPrice, // 가장 낮은 판매가
        Long highestBuyPrice // 가장 높은 구매가
) {
}
