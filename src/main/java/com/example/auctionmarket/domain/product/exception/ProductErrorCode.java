package com.example.auctionmarket.domain.product.exception;

import com.example.auctionmarket.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ProductErrorCode implements ErrorCode {

    PRODUCT_ALREADY_SOLD(HttpStatus.NOT_FOUND, "PRODUCT_ALREADY_SOLD", "이미 판매된 제품입니다."),
    NOT_MY_PRODUCT(HttpStatus.UNAUTHORIZED, "NOT_MY_PRODUCT", "본인 제품이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    ProductErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
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
        return this.defaultMessage;
    }
}
