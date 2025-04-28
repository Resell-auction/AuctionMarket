package com.example.auctionmarket.domain.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CouponGiveRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Integer amount;

    public CouponGiveRequest() {} // 기본 생성자

}
