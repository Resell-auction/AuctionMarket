package com.example.auctionmarket.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.example.auctionmarket.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
	private ErrorCode errorCode;
	private HttpStatus status;

	public AuthException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
		this.status = errorCode.getHttpStatus();
	}


}

