package com.example.auctionmarket.domain.product.exception;

import com.example.auctionmarket.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {

    private final ErrorCode errorCode;

    public ProductException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

}