package com.example.auctionmarket.domain.auction.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import com.example.auctionmarket.common.entity.BaseEntity;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.exception.AuctionErrorCode;
import com.example.auctionmarket.domain.auction.exception.AuctionException;
import com.example.auctionmarket.domain.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startTime;

    //경매 지속 시간
    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "Auction_Status")
    private AuctionStatus status;

    //경매 정보를 저장하기 위한 메서드
    @Builder
    public Auction(Product product, Long minPrice, LocalDateTime startTime, Long minutes) {
        this.product = product;
        this.minPrice = minPrice;
        this.maxPrice = minPrice;//생성 되었을 떄는 기본 최대가는 최소가하고 동일하다
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(minutes);
        this.endTime = this.getStartTime().plus(Duration.ofMinutes(minutes));
        this.status = AuctionStatus.PENDING;//생성 되었을 때는 기본으로 경매 진행 전 상태이다.
    }

    //경매 낙찰자 정보 저장
    public void increaseMaxPrice(Long consumerId, Long increasePrice) {
        //경매 낙찰가 증가
        if(this.maxPrice>=increasePrice){
            throw new AuctionException(AuctionErrorCode.INVALID_BID_PRICE);
        }

        this.maxPrice = increasePrice;
        this.consumerId = consumerId;
    }

    //시작 시간 수정 함수
    public void updateStartTime(LocalDateTime newStartTime) {
        this.startTime = newStartTime;
        this.endTime = newStartTime.plus(this.duration);
    }

    //최소 가격 수정 함수
    public void updateMinPrice(Long updateMinPrice) {
        this.minPrice = updateMinPrice;
        this.maxPrice = updateMinPrice;
    }

    public void updateStatus(AuctionStatus status) {
        this.status = status;
    }
}
