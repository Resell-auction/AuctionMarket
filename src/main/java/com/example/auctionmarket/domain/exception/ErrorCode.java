package com.example.auctionmarket.domain.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String getCode();

	HttpStatus getHttpStatus();

	String getDefaultMessage();
}

