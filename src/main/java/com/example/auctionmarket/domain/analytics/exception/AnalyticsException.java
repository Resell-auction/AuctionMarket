package com.example.auctionmarket.domain.analytics.exception;

import com.example.auctionmarket.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class AnalyticsException extends RuntimeException {

	private final ErrorCode errorCode;

	public AnalyticsException(ErrorCode errorCode) {
		super(errorCode.getDefaultMessage());
		this.errorCode = errorCode;
	}

	public AnalyticsException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getDefaultMessage(), cause);
		this.errorCode = errorCode;
	}
}