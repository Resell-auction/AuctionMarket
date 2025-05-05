package com.example.auctionmarket.domain.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequest {

    @NotBlank(message = "결제수단은 필수입니다")
    private String payType;

    @NotNull(message = "결제 금액은 필수입니다")
    private Long amount;

    private Long couponId;
}