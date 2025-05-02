package com.example.auctionmarket.domain.productimage.exception;

import com.example.auctionmarket.common.exception.ErrorCode;

public class ProductImageException extends RuntimeException {

    private final ErrorCode errorCode;

    public ProductImageException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
}
