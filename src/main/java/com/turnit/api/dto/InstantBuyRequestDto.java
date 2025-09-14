package com.turnit.api.dto;

public record InstantBuyRequestDto(
        Long buyerId,
        Long price // 구매자가 제시하는 최대 가격
) {
}