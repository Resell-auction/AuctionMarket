package com.example.auctionmarket.domain.payment.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class PaymentResponse {
    private Long amount;
    private LocalDateTime refundDeadline;
}