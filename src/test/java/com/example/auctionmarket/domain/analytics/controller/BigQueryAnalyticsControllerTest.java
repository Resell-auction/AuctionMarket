package com.example.auctionmarket.domain.analytics.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.service.BigQueryAnalyticsService;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ExtendWith(MockitoExtension.class)
class BigQueryAnalyticsControllerTest {

	@InjectMocks
	private BigQueryAnalyticsController bigQueryAnalyticsController;

	@Mock
	private BigQueryAnalyticsService bigQueryAnalyticsService;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();

		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		mockMvc = MockMvcBuilders.standaloneSetup(bigQueryAnalyticsController)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
			.build();
	}

	@Nested
	class getHourlyAverageBidTest {

		@Test
		void 유효한_요청_시_HourlyAverageBidResponse_반환() throws Exception {
			// given
			ProductCategory category = ProductCategory.SHOES;
			int days = 7;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder()
				.category(category)
				.days(days)
				.build();

			List<HourlyAverageBidResponse> mockResponse = Arrays.asList(
				new HourlyAverageBidResponse(9, 150000),
				new HourlyAverageBidResponse(10, 180000)
			);

			given(bigQueryAnalyticsService.getHourlyAverageBid(any(HourlyAverageBidRequest.class)))
				.willReturn(mockResponse);

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/hourly-average-bid")
				.param("category", category.name())
				.param("days", String.valueOf(days))
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data.length()").value(mockResponse.size()))
				.andExpect(jsonPath("$.data[0].hourOfDay").value(mockResponse.get(0).getHourOfDay()))
				.andExpect(jsonPath("$.data[0].averageWinningBid").value(mockResponse.get(0).getAverageWinningBid()))
				.andExpect(jsonPath("$.data[1].hourOfDay").value(mockResponse.get(1).getHourOfDay()))
				.andExpect(jsonPath("$.data[1].averageWinningBid").value(mockResponse.get(1).getAverageWinningBid()));

			then(bigQueryAnalyticsService).should(times(1)).getHourlyAverageBid(any(HourlyAverageBidRequest.class));
		}

		@Test
		void 시간별_데이터_없는_경우_빈_목록_반환() throws Exception {
			// given
			ProductCategory category = ProductCategory.BEAUTY;
			int days = 30;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder()
				.category(category)
				.days(days)
				.build();

			List<HourlyAverageBidResponse> mockResponse = Collections.emptyList();
			given(bigQueryAnalyticsService.getHourlyAverageBid(any(HourlyAverageBidRequest.class)))
				.willReturn(mockResponse);

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/hourly-average-bid")
				.param("category", category.name())
				.param("days", String.valueOf(days))
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data.length()").value(0));

			then(bigQueryAnalyticsService).should(times(1)).getHourlyAverageBid(any(HourlyAverageBidRequest.class));
		}

		@Test
		void 유효성_검사_실패_days가_1보다_작음() throws Exception {
			// given
			ProductCategory category = ProductCategory.SHOES;
			int invalidDays = 0;

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/hourly-average-bid")
				.param("category", category.name())
				.param("days", String.valueOf(invalidDays))
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").doesNotExist());

			then(bigQueryAnalyticsService).should(never()).getHourlyAverageBid(any(HourlyAverageBidRequest.class));
		}

		@Test
		void 유효성_검사_실패_category가_null() throws Exception {
			// given
			int days = 7;

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/hourly-average-bid")
				.param("days", String.valueOf(days))
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").doesNotExist());

			then(bigQueryAnalyticsService).should(never()).getHourlyAverageBid(any(HourlyAverageBidRequest.class));
		}
	}

	@Nested
	class GetDailyAverageBidTests {

		@Test
		void 유효한_요청_시_DailyAverageBidResponse_반환() throws Exception {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder()
				.category(category)
				.build();

			List<DailyAverageBidResponse> mockResponse = Arrays.asList(
				new DailyAverageBidResponse(LocalDate.of(2025, 4, 30), 95000),
				new DailyAverageBidResponse(LocalDate.of(2025, 5, 1), 110000)
			);
			given(bigQueryAnalyticsService.getDailyAverageBidByCategory(any(DailyAverageBidRequest.class)))
				.willReturn(mockResponse);

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/daily-average-bid")
				.param("category", category.name())
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data.length()").value(mockResponse.size()))
				.andExpect(jsonPath("$.data[0].auctionDate").value(mockResponse.get(0).getAuctionDate().toString()))
				.andExpect(jsonPath("$.data[0].averageWinningBid").value(mockResponse.get(0).getAverageWinningBid()))
				.andExpect(jsonPath("$.data[1].auctionDate").value(mockResponse.get(1).getAuctionDate().toString()))
				.andExpect(jsonPath("$.data[1].averageWinningBid").value(mockResponse.get(1).getAverageWinningBid()));
		}

		@Test
		void 일자별_데이터_없는_경우_빈_목록_반환() throws Exception {
			// given
			ProductCategory category = ProductCategory.LUXURY;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder()
				.category(category)
				.build();

			List<DailyAverageBidResponse> mockResponse = Collections.emptyList();
			given(bigQueryAnalyticsService.getDailyAverageBidByCategory(any(DailyAverageBidRequest.class)))
				.willReturn(mockResponse);

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/daily-average-bid")
				.param("category", category.name())
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data.length()").value(0));
		}

		@Test
		void 유효성_검사_실패_category가_null() throws Exception {
			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/daily-average-bid")
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isBadRequest());
		}
	}
}
