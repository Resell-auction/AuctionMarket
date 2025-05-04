package com.example.auctionmarket.domain.payment.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PayStatus {
    PENDING("결제 대기"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소"),
    REFUNDED("환불 완료");

    private final String description;

    PayStatus(String description) {
        this.description = description;
    }

    public static PayStatus of(String payStatus) {
        return Arrays.stream(PayStatus.values())
                .filter(p -> p.name().equalsIgnoreCase(payStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("현재 결제 상태는 : " + payStatus));
    }
}