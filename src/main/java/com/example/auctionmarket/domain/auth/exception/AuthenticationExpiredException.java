package com.example.auctionmarket.domain.auth.exception;

import jakarta.security.auth.message.AuthException;

public class AuthenticationExpiredException extends AuthException {
	public AuthenticationExpiredException() {
		super(String.valueOf(AuthErrorCode.AUTHENTICATION_EXPIRED));
	}
}
