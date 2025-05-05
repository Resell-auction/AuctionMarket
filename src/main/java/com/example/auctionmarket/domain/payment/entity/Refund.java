package com.example.auctionmarket.domain.payment.entity;

import com.example.auctionmarket.domain.payment.enums.PayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;
    private Long userId;
    private PayType payType;
    private String description;
    private LocalDateTime refundedAt;

    @Builder
    public Refund(Long paymentId, Long userId, PayType payType, String description, LocalDateTime refundedAt) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.payType = payType;
        this.description = description;
        this.refundedAt = refundedAt;
    }

    public void completeRefund(PayType payType, String description) {
        this.refundedAt = LocalDateTime.now();
        this.description = description;
        this.payType = payType;
    }
}