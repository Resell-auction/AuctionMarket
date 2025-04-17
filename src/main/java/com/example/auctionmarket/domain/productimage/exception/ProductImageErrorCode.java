package com.example.auctionmarket.domain.productimage.exception;

import com.example.auctionmarket.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ProductImageErrorCode implements ErrorCode {

    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "INVALID_IMAGE_TYPE", "파일 형식을 지원하지 않습니다. (jpg, jpeg, png, gif, pdf 업로드 가능)"),
    IMAGE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_NAME_NOT_FOUND", "파일 이름이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    ProductImageErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
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
