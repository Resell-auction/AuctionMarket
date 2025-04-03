package com.example.auctionmarket.domain.coupon.entity;

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

    //   private String condition; 뭐죠?

    public Coupon(String couponName, String description, double discountRate, LocalDateTime expiredAt, int amount){
        this.couponName=couponName;
        this.description=description;
        this.discountRate=discountRate;
        this.expiredAt=expiredAt;
        this.amount=amount;
    }

    public void update(String couponName, String description, double discountRate, LocalDateTime expiredAt){
        this.couponName=couponName;
        this.description=description;
        this.discountRate=discountRate;
        this.expiredAt=expiredAt;
    }


}
