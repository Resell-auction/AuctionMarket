package com.example.auctionmarket.domain.user.exception;

public class InvalidPasswordException extends UserException {
	public InvalidPasswordException() {
		super(UserErrorCode.INVALID_PASSWORD);
	}
}
