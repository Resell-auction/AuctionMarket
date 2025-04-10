package com.example.auctionmarket.domain.payment.event;

import com.example.auctionmarket.domain.auction.event.AuctionEndEvent;
import com.example.auctionmarket.domain.payment.entity.Payment;
import com.example.auctionmarket.domain.payment.enums.PayStatus;
import com.example.auctionmarket.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentRepository paymentRepository;

    @EventListener
    public void handleAuctionEnd(AuctionEndEvent event) {
        Payment payment = Payment.builder()
                .auction_id(event.getAuctionId())
                .user_id(event.getCunsumerId())
                .amount(event.getMaxPrice())
                .pay_status(PayStatus.PENDING)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        paymentRepository.save(payment);
    }
}