package com.example.auctionmarket.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.auctionmarket.common.exception.ErrorCode;

public class UserException extends RuntimeException {

	UserException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		HttpStatus status = errorCode.getHttpStatus();
	}
}
