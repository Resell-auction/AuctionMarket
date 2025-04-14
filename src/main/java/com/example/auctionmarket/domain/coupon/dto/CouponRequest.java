package com.example.auctionmarket.domain.coupon.dto;

import com.example.auctionmarket.domain.coupon.enums.CouponType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponRequest {
    private String couponName;

    private String description;

    private Long discountAmount;

    private LocalDateTime expiredAt;

    private int amount;

    private CouponType couponType;

}
