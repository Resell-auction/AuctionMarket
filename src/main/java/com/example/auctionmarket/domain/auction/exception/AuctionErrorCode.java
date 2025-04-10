package com.example.auctionmarket.domain.auction.exception;

import org.springframework.http.HttpStatus;

import com.example.auctionmarket.common.exception.ErrorCode;

public enum AuctionErrorCode implements ErrorCode {

    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "AUCTION_NOT_FOUND", "경매를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    NOT_AUCTION_OWNER(HttpStatus.BAD_REQUEST, "NOT_AUCTION_OWNER", "경매 소유자만 접근할 수 있습니다."),
    AUCTION_NOT_STARTED(HttpStatus.BAD_REQUEST, "AUCTION_NOT_STARTED", "경매가 시작되지 않았습니다."),
    AUCTION_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "AUCTION_ALREADY_STARTED", "이미 시작된 경매는 수정할 수 없습니다."),
    AUCTION_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "AUCTION_ALREADY_ENDED", "이미 종료된 경매입니다."),
    INVALID_BID_PRICE(HttpStatus.BAD_REQUEST, "INVALID_BID_PRICE", "유효하지 않은 입찰 가격입니다."),
    SELF_BID_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "SELF_BID_NOT_ALLOWED", "자신의 상품에는 입찰할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    AuctionErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
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
