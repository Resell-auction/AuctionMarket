package com.example.auctionmarket.domain.auth.exception;

public class InvalidJwtSignatureException extends AuthException {
	public InvalidJwtSignatureException() {
		super(AuthErrorCode.INVALID_JWT_SIGNATURE);
	}
}
