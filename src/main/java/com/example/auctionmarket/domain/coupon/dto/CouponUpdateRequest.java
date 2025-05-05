package com.example.auctionmarket.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponUpdateRequest {
    
    private String couponName;

    private String description;

    private Long discountAmount;

    private LocalDateTime expiredAt;
}
