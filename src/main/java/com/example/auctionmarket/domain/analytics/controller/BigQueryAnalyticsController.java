package com.example.auctionmarket.domain.analytics.controller;

import java.util.List;

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

@RestController
@RequestMapping("/v1/analytics/bigquery")
@RequiredArgsConstructor
public class BigQueryAnalyticsController {

	private final BigQueryAnalyticsService bigQueryAnalyticsService;

	// 시간별 평균 낙찰가
	@GetMapping("/hourly-average-bid")
	public Response<List<HourlyAverageBidResponse>> getHourlyAverageBid(
		@Valid @ModelAttribute HourlyAverageBidRequest request
	) {
		List<HourlyAverageBidResponse> responses = bigQueryAnalyticsService.getHourlyAverageBid(request);
		return Response.of(responses);
	}

	// 일자별 평균 낙찰가
	@GetMapping("/daily-average-bid")
	public Response<List<DailyAverageBidResponse>> getDailyAverageBidByCategory(
		@Valid @ModelAttribute DailyAverageBidRequest request
	) {
		List<DailyAverageBidResponse> responses = bigQueryAnalyticsService.getDailyAverageBidByCategory(request);
		return Response.of(responses);
	}
}