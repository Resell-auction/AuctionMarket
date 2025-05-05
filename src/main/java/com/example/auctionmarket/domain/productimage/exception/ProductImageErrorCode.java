package com.example.auctionmarket.domain.productimage.exception;

import org.springframework.http.HttpStatus;

import com.example.auctionmarket.common.exception.ErrorCode;

public enum ProductImageErrorCode implements ErrorCode {

    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "INVALID_IMAGE_TYPE", "파일 형식을 지원하지 않습니다. (jpg, jpeg, png, gif, pdf 업로드 가능)"),
    IMAGE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_NAME_NOT_FOUND", "파일 이름이 존재하지 않습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_NOT_FOUND", "이미지가 존재하지 않습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_FAILED", "이미지 업로드 중 오류가 발생했습니다.");

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
