package com.turnit.api.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trades")
public class Trade extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Product:Trade =1:N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // SellBid:Trade = 1:1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_bid_id", nullable = false, unique = true)
    private SellBid sellBid;

    // User(1) : Trade(N) 관계 (구매자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(nullable = false)
    private Long price; // 최종 거래가

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Trade(Product product, SellBid sellBid, User buyer, Long price, LocalDateTime createdAt) {
        // 기존 빌더 생성자 내용
        this.product = product;
        this.sellBid = sellBid;
        this.buyer = buyer;
        this.price = price;
        this.createdAt = createdAt; // 이 줄을 추가해야 합니다.
    }
}