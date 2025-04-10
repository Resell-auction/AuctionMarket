package com.example.auctionmarket.domain.payment.enums;

import java.util.Arrays;

public enum PayType {
    POINT;

    public static PayType of(String payType) {
        return Arrays.stream(PayType.values())
                .filter(t->t.name().equalsIgnoreCase(payType))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("Invalid pay type: " + payType));
    }
}

