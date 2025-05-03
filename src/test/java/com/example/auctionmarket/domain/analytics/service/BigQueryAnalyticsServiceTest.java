package com.example.auctionmarket.domain.analytics.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.repository.BigQueryRepository;
import com.example.auctionmarket.domain.product.enums.ProductCategory;

@ExtendWith(MockitoExtension.class)
class BigQueryAnalyticsServiceTest {

	@Mock
	private BigQueryRepository bigQueryRepository;

	@InjectMocks
	private BigQueryAnalyticsService bigQueryAnalyticsService;

	@Nested
	class GetHourlyAverageBidLogicTests {

		@Test
		void Hourly_Repository_호출_확인() {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			int days = 7;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder()
				.category(category)
				.days(days)
				.build();

			List<HourlyAverageBidResponse> mockResponse = Arrays.asList(
				new HourlyAverageBidResponse(10, 150000),
				new HourlyAverageBidResponse(11, 165000)
			);

			given(bigQueryRepository.findHourlyAverageBid(category, days)).willReturn(mockResponse);

			// when
			List<HourlyAverageBidResponse> actualResponse = bigQueryAnalyticsService.getHourlyAverageBid(request);

			// then
			assertThat(actualResponse).isNotNull();
			assertThat(actualResponse).hasSize(mockResponse.size());
			assertThat(actualResponse).isEqualTo(mockResponse);

			verify(bigQueryRepository, times(1)).findHourlyAverageBid(category, days);
		}
	}

	@Nested
	class GetDailyAverageBidByCategoryLogicTests {

		@Test
		void Daily_Repository_호출_확인() {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder()
				.category(category)
				.build();

			List<DailyAverageBidResponse> mockResponse = Arrays.asList(
				new DailyAverageBidResponse(LocalDate.of(2025, 5, 1), 50000),
				new DailyAverageBidResponse(LocalDate.of(2025, 5, 2), 55000)
			);

			given(bigQueryRepository.findDailyAverageBidByCategory(category)).willReturn(mockResponse);

			// when
			List<DailyAverageBidResponse> actualResponse = bigQueryAnalyticsService.getDailyAverageBidByCategory(request);

			// then
			assertThat(actualResponse).isNotNull();
			assertThat(actualResponse).hasSize(mockResponse.size());
			assertThat(actualResponse).isEqualTo(mockResponse);

			verify(bigQueryRepository, times(1)).findDailyAverageBidByCategory(category);
		}
	}
}