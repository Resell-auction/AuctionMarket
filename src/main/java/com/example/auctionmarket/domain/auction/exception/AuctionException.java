package com.example.auctionmarket.domain.auction.exception;

import com.example.auctionmarket.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class AuctionException extends RuntimeException{

    private final ErrorCode errorCode;

    public AuctionException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
