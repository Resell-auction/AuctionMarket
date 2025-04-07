package com.example.auctionmarket.domain.user.exception;

public class AlreadyExistsEmailException extends UserException {
	public AlreadyExistsEmailException() {
		super(UserErrorCode.ALREADY_EXISTS_EMAIL);
	}
}
