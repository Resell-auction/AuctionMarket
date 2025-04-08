package com.example.auctionmarket.domain.payment.dto.request;

import lombok.Getter;

@Getter
public class PaymentRequest {

    private Long userId;
    private String pay_type;
    private Long amount;
}
