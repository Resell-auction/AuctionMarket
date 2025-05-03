package com.example.auctionmarket.domain.analytics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.repository.BigQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BigQueryAnalyticsService {

	private final BigQueryRepository bigQueryRepository;

	public List<HourlyAverageBidResponse> getHourlyAverageBid(HourlyAverageBidRequest request) {
		List<HourlyAverageBidResponse> result = bigQueryRepository.findHourlyAverageBid(request.getCategory(),
			request.getDays());
		return result;
	}

	public List<DailyAverageBidResponse> getDailyAverageBidByCategory(DailyAverageBidRequest request) {
		List<DailyAverageBidResponse> result = bigQueryRepository.findDailyAverageBidByCategory(request.getCategory());
		return result;
	}
}