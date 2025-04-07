package com.example.auctionmarket.domain.coupon.entity;

import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name="coupons")
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    private String description;

    private double discountRate;

    private LocalDateTime expiredAt;

    private int amount;

    private CouponStatus couponStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //   private String condition;

    public Coupon(String couponName, String description, double discountRate, LocalDateTime expiredAt, int amount){
        this.couponName=couponName;
        this.description=description;
        this.discountRate=discountRate;
        this.expiredAt=expiredAt;
        this.amount=amount;
        this.couponStatus=CouponStatus.VALID;
    }

    public void update(String couponName, String description, double discountRate, LocalDateTime expiredAt){
        this.couponName=couponName;
        this.description=description;
        this.discountRate=discountRate;
        this.expiredAt=expiredAt;
    }

    public void expiredCoupon(){
        this.couponStatus=CouponStatus.EXPIRED;
    }

    public void discountCoupon(int amount){
        this.amount= this.amount-amount;
    }


}
