package com.example.auctionmarket.domain.payment.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.payment.dto.request.PaymentRequest;
import com.example.auctionmarket.domain.payment.dto.request.RefundRequest;
import com.example.auctionmarket.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 유저에게 결제하라고 요청이 들어온 상황
    @PostMapping("/{paymentId}")
    public ResponseEntity<Void> pay(
            @PathVariable Long paymentId,
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        paymentService.confirmPayment(paymentId, request, authUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Void> refund(
            @PathVariable Long paymentId,
            @RequestBody RefundRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        paymentService.refundPayment(paymentId,request, authUser);
        return ResponseEntity.ok().build();
    }
}
