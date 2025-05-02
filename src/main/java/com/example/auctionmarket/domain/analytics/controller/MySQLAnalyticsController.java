package com.example.auctionmarket.domain.analytics.controller;

import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.service.MySQLAnalyticsService; // MySQL 서비스 사용
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/analytics/mysql") // 경로 변경 (BigQuery와 구분)
@RequiredArgsConstructor
@Slf4j
public class MySQLAnalyticsController {

	private final MySQLAnalyticsService mySQLAnalyticsService; // MySQL 서비스 주입

	/**
	 * 시간별 평균 낙찰가 (MySQL)
	 * @param request 카테고리, 조회 기간(일)
	 * @return 시간대별 평균 낙찰가 리스트
	 */
	@GetMapping("/hourly-average-bid")
	public ResponseEntity<Response<List<HourlyAverageBidResponse>>> getHourlyAverageBid(
		@Valid @ModelAttribute HourlyAverageBidRequest request
	) {
		try {
			List<HourlyAverageBidResponse> responses = mySQLAnalyticsService.getHourlyAverageBid(request);
			return ResponseEntity.ok(Response.of(responses));
		} catch (Exception e) { // 일반적인 예외 처리 (테스트용)
			log.error("MySQL getHourlyAverageBid 에서 에러 발생", e);
			// 실제 운영 코드에서는 더 구체적인 예외 처리 필요
			return null;
		}
	}

	/**
	 * 일자별 평균 낙찰가 (MySQL)
	 * @param request 카테고리
	 * @return 일자별 평균 낙찰가 리스트 (최근 1년)
	 */
	@GetMapping("/daily-average-bid")
	public ResponseEntity<Response<List<DailyAverageBidResponse>>> getDailyAverageBidByCategory(
		@Valid @ModelAttribute DailyAverageBidRequest request
	) {
		try {
			List<DailyAverageBidResponse> responses = mySQLAnalyticsService.getDailyAverageBidByCategory(request);
			return ResponseEntity.ok(Response.of(responses));
		} catch (Exception e) { // 일반적인 예외 처리 (테스트용)
			log.error("MySQL getDailyAverageBidByCategory 에서 에러 발생", e);
			// 실제 운영 코드에서는 더 구체적인 예외 처리 필요
			return null;
		}
	}
}