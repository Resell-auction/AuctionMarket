package com.example.auctionmarket.domain.coupon.dto;

import lombok.Getter;

@Getter
public class CouponGiveRequest {
    private Long userId;

    private int amount;

}
