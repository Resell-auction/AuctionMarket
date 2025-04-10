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

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
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
