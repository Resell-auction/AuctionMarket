package com.example.auctionmarket.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CouponRequest {
    private String couponName;

    private String description;

    private double discountRate;

    private LocalDateTime expiredAt;

    private int amount;

}
