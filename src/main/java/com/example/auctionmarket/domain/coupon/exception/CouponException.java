package com.example.auctionmarket.domain.coupon.exception;

import com.example.auctionmarket.common.exception.ErrorCode;

public class CouponException extends RuntimeException {

    private final ErrorCode errorCode;

    public CouponException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
