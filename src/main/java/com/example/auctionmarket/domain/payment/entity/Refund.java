package com.example.auctionmarket.domain.payment.entity;

import com.example.auctionmarket.domain.payment.enums.PayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Refund {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    private PayType payType;
    private String description;

    private LocalDateTime refundedAt;

    @Builder
    public Refund(Payment payment, PayType payType, String description, LocalDateTime refundedAt) {
        this.payment = payment;
        this.payType = payType;
        this.description = description;
        this.refundedAt = refundedAt;
    }
}
