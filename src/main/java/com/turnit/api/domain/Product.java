package com.turnit.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String modelNumber;

    private Long releasePrice;

    private String imageUrl;

    @Builder
    public Product(String name, String modelNumber, Long releasePrice, String imageUrl) {
        this.name = name;
        this.modelNumber = modelNumber;
        this.releasePrice = releasePrice;
        this.imageUrl = imageUrl;
    }
}