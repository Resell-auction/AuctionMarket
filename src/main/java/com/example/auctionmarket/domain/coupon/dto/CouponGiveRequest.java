package com.example.auctionmarket.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CouponGiveRequest {
    private Long userId;

    private int amount;

}
