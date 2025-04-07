package com.example.auctionmarket.domain.user.exception;

public class InvalidUserRoleException extends UserException {
	public InvalidUserRoleException() {
		super(UserErrorCode.INVALID_USER_ROLE);
	}
}

