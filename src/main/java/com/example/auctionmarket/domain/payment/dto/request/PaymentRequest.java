package com.example.auctionmarket.domain.payment.dto.request;

import lombok.Getter;

@Getter
public class PaymentRequest {

    private String payType;
    private Long amount;
}
