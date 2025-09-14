package com.turnit.api.controller;

import com.turnit.api.dto.BidRequestDto;
import com.turnit.api.dto.InstantBuyRequestDto;
import com.turnit.api.dto.OrderBookResponseDto;
import com.turnit.api.service.BiddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/{productId}")
public class BiddingController {

    private final BiddingService biddingService;

    @PostMapping("/bids")
    public ResponseEntity<Void> createBid(@PathVariable Long productId, @RequestBody BidRequestDto requestDto) {
        // requestDto에서 size()를 가져와서 전달합니다.
        biddingService.createBid(productId, requestDto.price(), requestDto.userId(), requestDto.size(), requestDto.bidType());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bids/orderbook")
    public ResponseEntity<OrderBookResponseDto> getOrderBook(@PathVariable Long productId) {
        OrderBookResponseDto orderBook = biddingService.getOrderBook(productId);
        return ResponseEntity.ok(orderBook);
    }

    @PostMapping("/instant-buy")
    public ResponseEntity<Void> instantBuy(@PathVariable Long productId, @RequestBody InstantBuyRequestDto requestDto) {
        biddingService.executeInstantBuy(productId, requestDto.buyerId(), requestDto.price());
        return ResponseEntity.ok().build();
    }
}