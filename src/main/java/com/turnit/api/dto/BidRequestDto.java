package com.turnit.api.dto;

import com.turnit.api.domain.BidType;

public record BidRequestDto(
        Long userId,
        Long price,
        String size,
        BidType bidType
) {
}
