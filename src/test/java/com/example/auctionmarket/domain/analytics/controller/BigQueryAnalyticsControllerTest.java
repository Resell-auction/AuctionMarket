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
import org.junit.jupiter.api.DisplayName;
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

	// MockMvc: HTTP 요청 테스트
	private MockMvc mockMvc;

	// ObjectMapper: JSON 직렬화/역직렬화
	private ObjectMapper objectMapper;
	// private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();

		// 날짜, 시간 타입을 숫자 타임스탬프 대신 표준 문자열로 직렬화
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		mockMvc = MockMvcBuilders.standaloneSetup(bigQueryAnalyticsController)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
			.build();
	}

	@Nested
	@DisplayName("getHourlyAverageBid 테스트")
	class GetHourlyAverageBidTests {

		@Test
		@DisplayName("유효한 요청 시 HourlyAverageBidResponse 반환")
		void getHourlyAverageBid_Success() throws Exception {
			// given
			ProductCategory category = ProductCategory.SHOES;
			int days = 7;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder()
				.category(category)
				.days(days)
				.build();

			List<HourlyAverageBidResponse> mockResponse = Arrays.asList(
				new HourlyAverageBidResponse(9, 150000.50),
				new HourlyAverageBidResponse(10, 180000.75)
			);

			given(bigQueryAnalyticsService.getHourlyAverageBid(any(HourlyAverageBidRequest.class)))
				.willReturn(mockResponse);

			// when
			// category 와 days 파라미터를 쿼리 스트링으로 전달
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/hourly-average-bid")
				.param("category", category.name())
				.param("days", String.valueOf(days))
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isOk()) // HTTP 상태 코드 200 확인
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) // 응답 컨텐츠 타입 확인
				.andExpect(jsonPath("$.data").isArray()) // 응답 본문의 data 필드가 배열인지 확인
				.andExpect(jsonPath("$.data.length()").value(mockResponse.size())) // 배열 크기 확인
				.andExpect(jsonPath("$.data[0].hourOfDay").value(mockResponse.get(0).getHourOfDay())) // 첫 번째 요소 검증
				.andExpect(jsonPath("$.data[0].averageWinningBid").value(mockResponse.get(0).getAverageWinningBid()))
				.andExpect(jsonPath("$.data[1].hourOfDay").value(mockResponse.get(1).getHourOfDay())) // 두 번째 요소 검증
				.andExpect(jsonPath("$.data[1].averageWinningBid").value(mockResponse.get(1).getAverageWinningBid()));
		}

		@Test
		@DisplayName("데이터 없는 경우 빈 목록 반환")
		void getHourlyAverageBid_Success_NoData() throws Exception {
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
				.andExpect(jsonPath("$.data.length()").value(0)); // 빈 배열 확인
		}

		@Test
		@DisplayName("서비스에서 InterruptedException 발생 시 500 반환")
		void getHourlyAverageBid_Failure_InterruptedException() throws Exception {
			// given
			ProductCategory category = ProductCategory.SHOES;
			int days = 7;

			given(bigQueryAnalyticsService.getHourlyAverageBid(any(HourlyAverageBidRequest.class)))
				.willThrow(new InterruptedException());

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/hourly-average-bid")
				.param("category", category.name())
				.param("days", String.valueOf(days))
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.data").doesNotExist()); // 실패 시 data 필드는 없음
		}

		@Test
		@DisplayName("유효성 검사 실패: days < 1")
		void getHourlyAverageBid_Failure_Validation_Days() throws Exception {
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
		}

		@Test
		@DisplayName("유효성 검사 실패: category 널")
		void getHourlyAverageBid_Failure_Validation_Category() throws Exception {
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

		}
	}


	@Nested
	@DisplayName("getDailyAverageBidByCategory 테스트")
	class GetDailyAverageBidTests {

		@Test
		@DisplayName("유효한 요청 시 DailyAverageBidResponse 반환")
		void getDailyAverageBid_Success() throws Exception {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder()
				.category(category)
				.build();

			List<DailyAverageBidResponse> mockResponse = Arrays.asList(
				new DailyAverageBidResponse(LocalDate.of(2025, 4, 30), 95000.0),
				new DailyAverageBidResponse(LocalDate.of(2025, 5, 1), 110000.0)
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
		@DisplayName("데이터 없는 경우 빈 목록 반환")
		void getDailyAverageBid_Success_NoData() throws Exception {
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
				.andExpect(jsonPath("$.data.length()").value(0)); // 빈 배열 확인
		}

		@Test
		@DisplayName("InterruptedException 발생 시 500 반환")
		void getDailyAverageBid_Failure_InterruptedException() throws Exception {
			// given
			ProductCategory category = ProductCategory.CLOTHES;

			// Mock 서비스에서 InterruptedException 발생하도록 설정
			given(bigQueryAnalyticsService.getDailyAverageBidByCategory(any(DailyAverageBidRequest.class)))
				.willThrow(new InterruptedException());

			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/daily-average-bid")
				.param("category", category.name())
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isInternalServerError());
		}

		@Test
		@DisplayName("유효성 검사 실패: category 널")
		void getDailyAverageBid_Failure_Validation_Category() throws Exception {
			// when
			ResultActions resultActions = mockMvc.perform(get("/v1/analytics/bigquery/daily-average-bid")
				.accept(MediaType.APPLICATION_JSON));

			// then
			resultActions
				.andExpect(status().isBadRequest());
		}
	}
}
