package com.example.auctionmarket.domain.coupon.exception;

import com.example.auctionmarket.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CouponErrorCode implements ErrorCode {

    NOT_ADMIN_AUTHORITY(HttpStatus.FORBIDDEN, "NOT_ADMIN_AUTHORITY", "관리자만 접근할 수 있습니다."),
    NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "NOT_FOUND_COUPON", "찾는 쿠폰이 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NOT_FOUND_USER", "찾는 회원이 없습니다."),
    OUT_OF_COUPON(HttpStatus.CONFLICT, "OUT_OF_COUPON", "쿠폰이 모두 소진되었습니다."),
    ISSUED_COUPON(HttpStatus.CONFLICT, "ISSUED_COUPON", "이미 발급받은 쿠폰입니다."),
    DUPLICATE_COUPON(HttpStatus.UNPROCESSABLE_ENTITY, "DUPLICATE_COUPON", "해당 쿠폰은 1장만 발급가능합니다");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;

    CouponErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.status;
    }

    @Override
    public String getDefaultMessage() {
        return this.defaultMessage;
    }
}

