package com.example.auctionmarket.domain.payment.exception;

import com.example.auctionmarket.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found", "결제 정보를 찾을 수 없습니다"),
    NOT_PAYMENT_OWNER(HttpStatus.BAD_REQUEST, "NOT_PAYMENT_OWNER", "해당 결제는 낙찰자만 진행할 수 있습니다"),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_PAYMENT_AMOUNT", "결제 금액이 일치하지 않습니다."),
    PAYMENT_ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "PAYMENT_ALREADY_REFUNDED", "이미 환불된 결제입니다."),
    INVALID_PAY_TYPE(HttpStatus.BAD_REQUEST, "INVALID_PAY_TYPE", "결제 수단이 일치하지 않습니다."),
    ALREADY_COMPLETED_PAYMENT(HttpStatus.BAD_REQUEST, "ALREADY_COMPLETED_PAYMENT", "결제가 이미 완료되었습니다"),
    DEADLINE_EXPIRED_PAYMENT(HttpStatus.BAD_REQUEST,"DEADLINE_EXPIRED_PAYMENT", "결제 기한이 지났습니다"),
    NOT_COMPLETED_PAYMENT(HttpStatus.BAD_REQUEST,"NOT_COMPLETED_PAYMENT", "결제가 완료되지 않아 환불할 수 없습니다"),
    NOT_FOUND_AUCTION(HttpStatus.NOT_FOUND,"NOT_FOUND_AUCTION", "해당 경매를 확인할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getDefaultMessage() {
        return this.message;
    }
}
