package com.example.auctionmarket.domain.auction.entity;

import com.example.auctionmarket.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "auctions")
public class Auction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne
//    @JoinColumn(name = "product_id")
//    private Product product

    //경매 최소 금액
    private Long minPrice;

    //경매 최대 금액
    private Long maxPrice;

    //경매 지속 시간
    @Column(nullable = false)
    private Duration duration;

    public Auction(/*Product product, */Long minPrice, Long maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public static Auction of(Long minPrice, Long seconds){
        Auction auction = new Auction();
        auction.minPrice = minPrice;
        auction.duration = Duration.ofSeconds(seconds);
        return auction;
    }
}
