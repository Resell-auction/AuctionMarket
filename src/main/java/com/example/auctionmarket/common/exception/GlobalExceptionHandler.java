package com.example.auctionmarket.common.exception;

import java.util.ArrayList;
import java.util.List;

import com.example.auctionmarket.domain.auction.exception.AuctionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.auctionmarket.common.response.ErrorResponse;
import com.example.auctionmarket.common.response.ValidResponse;
import com.example.auctionmarket.domain.user.exception.EmailAccessDeniedException;
import com.example.auctionmarket.domain.user.exception.EmailNotFoundException;
import com.example.auctionmarket.domain.user.exception.InvalidPasswordException;
import com.example.auctionmarket.domain.user.exception.UserErrorCode;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<ValidResponse>> invalidRequestExceptionException(
		MethodArgumentNotValidException ex) {
		List<ValidResponse> errors = new ArrayList<>();

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		for (FieldError err : fieldErrors) {
			errors.add(ValidResponse.of(err.getField(), err.getDefaultMessage()));
		}
		log.error(" " + ex);

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 로그인 시 비밀번호 불일치 예외 처리 핸들러
	 */
	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException e) {
		// 실무적인 로그 메시지 기록 (예: 어떤 이메일 시도였는지 명시 - 필요시)
		// 주의: 실제 비밀번호는 절대 로그에 남기지 마세요.
		log.warn("Login failed: Invalid password attempt.", e); // e.getMessage() 대신 고정 메시지 사용 또는 context 정보 추가
		// ErrorCode enum 에서 메시지와 코드 가져오기 (UserErrorCode.java 에 정의된 값 활용)
		ErrorCode errorCode = UserErrorCode.INVALID_PASSWORD;
		ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getDefaultMessage());
		// 로그인 실패는 보통 401 Unauthorized 또는 400 Bad Request 반환
		return new ResponseEntity<>(response, errorCode.getHttpStatus()); // UserErrorCode에 정의된 HttpStatus 사용
	}

	/**
	 * 로그인 시 이메일 없음 예외 처리 핸들러
	 */
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEmailNotFoundException(EmailNotFoundException e) {
		log.warn("Login failed: Email not found.", e);
		ErrorCode errorCode = UserErrorCode.EMAIL_NOT_FOUND;
		ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getDefaultMessage());
		return new ResponseEntity<>(response, errorCode.getHttpStatus());
	}

	/**
	 * 로그인 시 접근 거부 (예: 탈퇴 회원) 예외 처리 핸들러
	 */
	@ExceptionHandler(EmailAccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleEmailAccessDeniedException(EmailAccessDeniedException e) {
		log.warn("Login failed: Email access denied (e.g., deactivated user).", e);
		ErrorCode errorCode = UserErrorCode.EMAIL_ACCESS_DENIED;
		ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getDefaultMessage());
		return new ResponseEntity<>(response, errorCode.getHttpStatus());
	}


	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ErrorResponse handleGlobalException(Exception e) {
		log.error("Exception : {}", e.getMessage(), e);
		return ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.");
	}


	@ExceptionHandler(AuctionException.class)
	public ResponseEntity<ErrorResponse> handleAuctionException(AuctionException e) {
		ErrorCode errorCode = e.getErrorCode();
		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(ErrorResponse.of(errorCode.getCode(), errorCode.getDefaultMessage()));
	}
}
