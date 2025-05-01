package com.example.auctionmarket.domain.analytics.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobStatus;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableResult;

@ExtendWith(MockitoExtension.class)
class BigQueryAnalyticsServiceTest {

	@Mock
	private BigQuery bigquery; // BigQuery 클라이언트 객체 Mock

	@Mock
	private Job queryJob; // BigQuery Job 객체 Mock

	@Mock
	private JobStatus jobStatus; // Job 상태 Mock

	@Mock
	private BigQueryError bigQueryError; // Job 에러 Mock

	@Mock
	private TableResult tableResult; // 쿼리 결과 Mock

	// 프로퍼티 생성자 주입이 필요하므로 @InjectMocks 안쓰고 수동으로 호출해서 Mock 객체 주입
	private BigQueryAnalyticsService bigQueryAnalyticsService;

	// 프로퍼티 설정값
	private final String testProjectId = "test-project";
	private final String testDatasetName = "test_dataset";
	private final String testTableName = "test_table";


	@BeforeEach
	void setUp() throws InterruptedException {
		// @InjectMocks 가 생성자 주입을 처리하지 못하는 경우, 수동으로 생성자를 호출하여 Mock 객체 주입
		bigQueryAnalyticsService = new BigQueryAnalyticsService(bigquery, testProjectId, testDatasetName, testTableName);

		// lenient(): 해당 stubbing 이 모든 테스트에서 사용되지 않더라도 UnnecessaryStubbingException 오류를 발생시키지 않도록함
		lenient().when(queryJob.waitFor()).thenReturn(queryJob);
		lenient().when(queryJob.getStatus()).thenReturn(jobStatus);
	}

	@Nested // 중첩 클래스로 테스트를 그룹 방식으로 구성
	@DisplayName("getHourlyAverageBid 테스트")
	class GetHourlyAverageBidTests {

		@Test
		@DisplayName("BigQuery 조회 결과를 HourlyAverageBidResponse 리스트로 변환 성공")
		void getHourlyAverageBid_Success() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.ACCESSORY;
			int days = 30;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder()
				.category(category)
				.days(days)
				.build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(jobStatus.getError()).willReturn(null);
			given(queryJob.getQueryResults()).willReturn(tableResult);

			// Mock tableResult 데이터 설정
			// FieldValueList: BigQuery 쿼리 결과의 단일 행(row) 데이터 나타냄
			FieldValueList row1 = FieldValueList.of(Arrays.asList(
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, "11"),
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, "55000.0")
			), FieldList.of(Field.of("hour_of_day", StandardSQLTypeName.INT64), Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64)));
			FieldValueList row2 = FieldValueList.of(Arrays.asList(
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, "15"),
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, "72000.0")
			), FieldList.of(Field.of("hour_of_day", StandardSQLTypeName.INT64), Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64)));

			given(tableResult.iterateAll()).willReturn(Arrays.asList(row1, row2));

			// when
			List<HourlyAverageBidResponse> responses = bigQueryAnalyticsService.getHourlyAverageBid(request);

			// then
			assertThat(responses).isNotNull();
			assertThat(responses).hasSize(2);

			assertThat(responses.get(0).getHourOfDay()).isEqualTo(11);
			assertThat(responses.get(0).getAverageWinningBid()).isEqualTo(55000.0);
			assertThat(responses.get(1).getHourOfDay()).isEqualTo(15);
			assertThat(responses.get(1).getAverageWinningBid()).isEqualTo(72000.0);

			// 각 메소드가 1번 호출되었는지 검증
			then(bigquery).should(times(1)).create(any(JobInfo.class));
			then(queryJob).should(times(1)).waitFor();
			then(queryJob).should(times(1)).getQueryResults();
		}

		@Test
		@DisplayName("BigQuery 조회 결과 중 average_winning_bid 가 NULL 인 경우 0.0으로 처리")
		void getHourlyAverageBid_Success_NullAverageBid() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.SHOES;
			int days = 3;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder().category(category).days(days).build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(jobStatus.getError()).willReturn(null);
			given(queryJob.getQueryResults()).willReturn(tableResult);

			FieldValue nullAvgBidValue = FieldValue.of(FieldValue.Attribute.PRIMITIVE, null);
			FieldValueList rowWithNull = FieldValueList.of(Arrays.asList(
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, "13"),
				nullAvgBidValue // NULL 값 주입
			), FieldList.of(Field.of("hour_of_day", StandardSQLTypeName.INT64), Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64)));

			given(tableResult.iterateAll()).willReturn(Collections.singletonList(rowWithNull));

			// when
			List<HourlyAverageBidResponse> responses = bigQueryAnalyticsService.getHourlyAverageBid(request);

			// then
			assertThat(responses).hasSize(1);
			assertThat(responses.get(0).getHourOfDay()).isEqualTo(13);
			assertThat(responses.get(0).getAverageWinningBid()).isEqualTo(0.0);
		}

		@Test
		@DisplayName("BigQuery 작업 중 에러 발생 시 RuntimeException 예외처리")
		void getHourlyAverageBid_Failure_BigQueryJobError() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.ACCESSORY;
			int days = 14;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder().category(category).days(days).build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(jobStatus.getError()).willReturn(bigQueryError);

			// when, then
			assertThatThrownBy(() -> bigQueryAnalyticsService.getHourlyAverageBid(request))
				.isInstanceOf(RuntimeException.class);

			// BigQuery 의 create, job.waitFor 호출되었는지 확인
			then(bigquery).should(times(1)).create(any(JobInfo.class));
			then(queryJob).should(times(1)).waitFor();
			// 에러 발생 시 getQueryResults 호출되지 않음
			then(queryJob).should(never()).getQueryResults();
		}

		@Test
		@DisplayName("Job 대기 중 InterruptedException 발생 시 예외처리")
		void getHourlyAverageBid_Failure_InterruptedException() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.ACCESSORY;
			int days = 14;
			HourlyAverageBidRequest request = HourlyAverageBidRequest.builder().category(category).days(days).build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(queryJob.waitFor()).willThrow(new InterruptedException());

			// when, then
			assertThatThrownBy(() -> bigQueryAnalyticsService.getHourlyAverageBid(request))
				.isInstanceOf(InterruptedException.class);

			then(bigquery).should(times(1)).create(any(JobInfo.class));
			then(queryJob).should(never()).getQueryResults();
		}
	}


	@Nested
	@DisplayName("getDailyAverageBidByCategory 테스트")
	class GetDailyAverageBidByCategoryTests {

		@Test
		@DisplayName("BigQuery 조회 결과를 DailyAverageBidResponse 리스트로 변환 성공")
		void getDailyAverageBidByCategory_Success() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.BAG;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder().category(category).build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(jobStatus.getError()).willReturn(null);
			given(queryJob.getQueryResults()).willReturn(tableResult);

			// Mock tableResult 데이터 설정
			LocalDate date1 = LocalDate.of(2025, 4, 15);
			LocalDate date2 = LocalDate.of(2025, 4, 16);
			String dateStr1 = date1.format(DateTimeFormatter.ISO_LOCAL_DATE);
			String dateStr2 = date2.format(DateTimeFormatter.ISO_LOCAL_DATE);

			FieldValueList row1 = FieldValueList.of(Arrays.asList(
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, dateStr1),
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, "250000.0")
			), FieldList.of(Field.of("auction_date", StandardSQLTypeName.DATE), Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64)));
			FieldValueList row2 = FieldValueList.of(Arrays.asList(
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, dateStr2),
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, "285000.5")
			), FieldList.of(Field.of("auction_date", StandardSQLTypeName.DATE), Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64)));


			given(tableResult.iterateAll()).willReturn(Arrays.asList(row1, row2));


			// when
			List<DailyAverageBidResponse> responses = bigQueryAnalyticsService.getDailyAverageBidByCategory(request);

			// then
			assertThat(responses).isNotNull();
			assertThat(responses).hasSize(2);

			assertThat(responses.get(0).getAuctionDate()).isEqualTo(date1);
			assertThat(responses.get(0).getAverageWinningBid()).isEqualTo(250000.0);
			assertThat(responses.get(1).getAuctionDate()).isEqualTo(date2);
			assertThat(responses.get(1).getAverageWinningBid()).isEqualTo(285000.5);

			then(bigquery).should(times(1)).create(any(JobInfo.class));
			then(queryJob).should(times(1)).waitFor();
			then(queryJob).should(times(1)).getQueryResults();
		}

		@Test
		@DisplayName("BigQuery 결과 중 average_winning_bid 가 NULL 인 경우 0.0으로 처리")
		void getDailyAverageBidByCategory_Success_NullAverageBid() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder().category(category).build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(jobStatus.getError()).willReturn(null);
			given(queryJob.getQueryResults()).willReturn(tableResult);

			LocalDate date = LocalDate.of(2025, 5, 1);
			String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
			FieldValue nullAvgBidValue = FieldValue.of(FieldValue.Attribute.PRIMITIVE, null);

			FieldValueList rowWithNull = FieldValueList.of(Arrays.asList(
				FieldValue.of(FieldValue.Attribute.PRIMITIVE, dateStr),
				nullAvgBidValue
			), FieldList.of(Field.of("auction_date", StandardSQLTypeName.DATE), Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64)));

			given(tableResult.iterateAll()).willReturn(Collections.singletonList(rowWithNull));

			// when
			List<DailyAverageBidResponse> responses = bigQueryAnalyticsService.getDailyAverageBidByCategory(request);

			// then
			assertThat(responses).hasSize(1);
			assertThat(responses.get(0).getAuctionDate()).isEqualTo(date);
			assertThat(responses.get(0).getAverageWinningBid()).isEqualTo(0.0);
		}

		@Test
		@DisplayName("BigQuery 작업 중 에러 발생 시 RuntimeException 예외 처리")
		void getDailyAverageBidByCategory_Failure_BigQueryJobError() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.BAG;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder().category(category).build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(jobStatus.getError()).willReturn(bigQueryError);

			// when, then
			assertThatThrownBy(() -> bigQueryAnalyticsService.getDailyAverageBidByCategory(request))
				.isInstanceOf(RuntimeException.class);

			then(bigquery).should(times(1)).create(any(JobInfo.class));
			then(queryJob).should(times(1)).waitFor();
			then(queryJob).should(never()).getQueryResults();
		}

		@Test
		@DisplayName("Job 대기 중 InterruptedException 발생 시 예외처리")
		void getDailyAverageBidByCategory_Failure_InterruptedException() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.BAG;
			DailyAverageBidRequest request = DailyAverageBidRequest.builder().category(category).build();

			given(bigquery.create(any(JobInfo.class))).willReturn(queryJob);
			given(queryJob.waitFor()).willThrow(new InterruptedException());

			// when & then
			assertThatThrownBy(() -> bigQueryAnalyticsService.getDailyAverageBidByCategory(request))
				.isInstanceOf(InterruptedException.class);

			then(bigquery).should(times(1)).create(any(JobInfo.class));
			then(queryJob).should(never()).getQueryResults();
		}
	}
}