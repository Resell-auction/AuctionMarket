package com.example.auctionmarket.domain.coupon.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponUpdateRequest {
    private String couponName;

    private String description;

    private double discountRate;

    private LocalDateTime expiredAt;
}
