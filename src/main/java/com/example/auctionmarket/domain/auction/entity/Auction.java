package com.example.auctionmarket.domain.auction.entity;

import com.example.auctionmarket.common.entity.BaseEntity;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "auctions")
public class Auction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    //낙찰자 ID
    @Column
    private Long consumerId;

    //경매 최소 금액
    @Column(nullable = false)
    private Long minPrice;

    //경매 최대 금액
    private Long maxPrice;

    //경매 시작 시간
    @Column(nullable = false)
    private LocalDateTime startTime;

    //경매 지속 시간
    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "Auction_Status")
    private AuctionStatus status;

    //경매 정보를 저장하기 위한 메서드
    public static Auction of(Product product, Long minPrice, LocalDateTime startTime, Long minutes) {
        Auction auction = new Auction();
        auction.product = product;
        auction.minPrice = minPrice;
        auction.maxPrice = minPrice;//생성 되었을 떄는 기본 최대가는 최소가하고 동일하다
        auction.startTime = startTime;
        auction.duration = Duration.ofMinutes(minutes);
        auction.endTime = auction.getStartTime().plus(Duration.ofMinutes(minutes));
        auction.status = AuctionStatus.PENDING;//생성 되었을 때는 기본으로 경매 진행 전 상태이다.
        return auction;
    }

    //경매 낙찰자 정보 저장
    public void increaseMaxPrice(Long consumerId, Long increasePrice) {
        this.consumerId = consumerId;

        //경매 낙찰가 증가
        if(this.maxPrice < increasePrice) {
            this.maxPrice = increasePrice;
        }
    }
}
