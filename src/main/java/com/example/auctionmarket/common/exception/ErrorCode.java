package com.example.auctionmarket.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String getCode();

	HttpStatus getHttpStatus();

	String getDefaultMessage();
}

