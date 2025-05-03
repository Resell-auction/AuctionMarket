package com.example.auctionmarket.domain.analytics.exception;

import org.springframework.http.HttpStatus;

import com.example.auctionmarket.common.exception.ErrorCode;

public enum AnalyticsErrorCode implements ErrorCode {

	BQ_QUERY_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BQ_QUERY_EXECUTION_FAILED", "BigQuery 데이터 조회 중 오류가 발생했습니다."),
	BQ_RESULT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BQ_RESULT_PROCESSING_FAILED", "BigQuery 데이터 처리(결과 파싱) 중 오류가 발생했습니다."),
	BQ_OPERATION_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "BQ_OPERATION_INTERRUPTED", "데이터 조회 작업이 중단되었습니다."),
	BQ_JOB_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BQ_JOB_FAILED", "BigQuery 작업 실행에 실패했습니다."),
	BQ_JOB_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "BQ_JOB_NOT_FOUND", "BigQuery 작업을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String defaultMessage;

	AnalyticsErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public String getDefaultMessage() {
		return this.defaultMessage;
	}
}