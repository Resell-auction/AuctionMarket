package com.example.auctionmarket.domain.coupon.dto;

import lombok.AllArgsConstructor;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CouponRequest {
    private String couponName;

    private String description;

    private Long discountAmount;

    private LocalDateTime expiredAt;

    private int amount;

    private CouponType couponType;

    public CouponRequest() {} // ✅ 기본 생성자 추가


}
