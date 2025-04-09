package com.example.auctionmarket.domain.payment.dto.request;

import lombok.Getter;

@Getter
public class RefundRequest {
    private String payType; // 결제했었던 결제수단
    private String description; // 환불 사유
}
