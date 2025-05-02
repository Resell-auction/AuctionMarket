package com.example.auctionmarket.domain.analytics.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.service.BigQueryAnalyticsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/analytics/bigquery")
@RequiredArgsConstructor
@Slf4j
public class BigQueryAnalyticsController {

	private final BigQueryAnalyticsService bigQueryAnalyticsService;

	// 시간별 평균 낙찰가
	@GetMapping("/hourly-average-bid")
	public ResponseEntity<Response<List<HourlyAverageBidResponse>>> getHourlyAverageBid(
		@Valid @ModelAttribute HourlyAverageBidRequest request
	) {
		// BigQuery 와 동기 방식으로 통신하기 때문에 InterruptedException 던질 가능성이 있음
		try {
			List<HourlyAverageBidResponse> responses = bigQueryAnalyticsService.getHourlyAverageBid(request);
			return ResponseEntity.ok(Response.of(responses));

		} catch (InterruptedException e) {

			log.error("getHourlyAverageBid 에서 InterruptedException 발생", e);
			Thread.currentThread().interrupt(); // 인터럽트 상태 복원
			return ResponseEntity.internalServerError().body(Response.of(null));
		}
	}

	// 일자별 평균 낙찰가
	@GetMapping("/daily-average-bid")
	public ResponseEntity<Response<List<DailyAverageBidResponse>>> getDailyAverageBidByCategory(
		@Valid @ModelAttribute DailyAverageBidRequest request
	) {
		try {
			List<DailyAverageBidResponse> responses = bigQueryAnalyticsService.getDailyAverageBidByCategory(request);
			return ResponseEntity.ok(Response.of(responses));

		} catch (InterruptedException e) {
			log.error("getDailyAverageBidByCategory 에서 InterruptedException 발생", e);
			Thread.currentThread().interrupt(); // 인터럽트 상태 복원
			return ResponseEntity.internalServerError().body(Response.of(null));
		}
	}

}
