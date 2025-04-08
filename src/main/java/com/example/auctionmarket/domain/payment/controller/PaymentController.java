package com.example.auctionmarket.domain.payment.controller;

import com.example.auctionmarket.domain.payment.dto.request.PaymentRequest;
import com.example.auctionmarket.domain.payment.dto.request.RefundRequest;
import com.example.auctionmarket.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 유저에게 결제하라고 요청이 들어온 상황
    @PostMapping("/v1/payments/{paymentId}")
    public ResponseEntity<String> pay(Long paymentId, @RequestBody PaymentRequest request) {
        paymentService.confirmPayment(paymentId, request);
        return ResponseEntity.ok("결제가 완료 되었습니다");
    }

    @PostMapping("/v1/payments/{paymentId}/refund")
    public ResponseEntity<String> refund(Long paymentId, @RequestBody RefundRequest request) {
        paymentService.refundPayment(paymentId,request);
        return ResponseEntity.ok("환불이 완료 되었습니다");
    }
}
