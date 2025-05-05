package com.example.auctionmarket.domain.payment.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.payment.dto.request.PaymentRequest;
import com.example.auctionmarket.domain.payment.dto.request.RefundRequest;
import com.example.auctionmarket.domain.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/payments/{paymentId}")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 유저에게 결제하라고 요청이 들어온 상황
    @PostMapping
    public Response<Void> pay(
            @PathVariable Long paymentId,
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        paymentService.confirmPayment(paymentId, request, authUser);
        return Response.empty();
    }

    @PostMapping("/refund")
    public Response<Void> refund(
            @PathVariable Long paymentId,
            @RequestBody RefundRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        paymentService.refundPayment(paymentId, request, authUser);
        return Response.empty();
    }
}