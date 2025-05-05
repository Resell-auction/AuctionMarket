package com.example.auctionmarket.domain.coupon.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponResponse {

    private final Long id;

    private final String couponName;

    private final String description;

    private final double discountRate;

    private final LocalDateTime expiredAt;

    private final int amount;

    public CouponResponse(Long id, String couponName, String description, double discountRate, LocalDateTime expiredAt, int amount) {
        this.id = id;
        this.couponName = couponName;
        this.description = description;
        this.discountRate = discountRate;
        this.expiredAt = expiredAt;
        this.amount = amount;
    }
}
