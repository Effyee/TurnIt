package com.turnit.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sell_bids")
public class SellBid extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User(1) : SellBid(N) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Product(1) : SellBid(N) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private Long price; // 판매 희망가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellBidStatus status; // 판매 상태 (ON_SALE, SOLD_OUT)

    @Builder
    public SellBid(User user, Product product, String size, Long price, SellBidStatus status) {
        this.user = user;
        this.product = product;
        this.size = size;
        this.price = price;
        this.status = status;
    }
}