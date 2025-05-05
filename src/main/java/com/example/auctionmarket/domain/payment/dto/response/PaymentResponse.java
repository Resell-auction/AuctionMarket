package com.example.auctionmarket.domain.payment.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResponse {
    private Long amount;
    private LocalDateTime refundDeadline;
}