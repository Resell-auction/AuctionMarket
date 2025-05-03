package com.example.auctionmarket.domain.analytics.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.exception.AnalyticsErrorCode;
import com.example.auctionmarket.domain.analytics.exception.AnalyticsException;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobStatus;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableResult;

@ExtendWith(MockitoExtension.class)
class BigQueryRepositoryImplTest {

	@Mock
	private BigQuery mockBigQuery;

	private BigQueryRepositoryImpl bigQueryRepository;

	private final String projectId = "test-project";
	private final String datasetName = "test_dataset";
	private final String tableName = "test_table";

	@BeforeEach
	void setUp() {
		bigQueryRepository = new BigQueryRepositoryImpl(mockBigQuery, projectId, datasetName, tableName);
	}

	@Nested
	class FindHourlyAverageBidTests {

		@Test
		void findHourlyAverageBid_BigQuery_조회_성공_및_결과_파싱_확인() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			int days = 7;

			Field hourField = Field.of("hour_of_day", StandardSQLTypeName.INT64);
			Field avgBidField = Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64);

			FieldValue hourValue1 = mock(FieldValue.class);
			when(hourValue1.getLongValue()).thenReturn(10L);
			FieldValue avgBidValue1 = mock(FieldValue.class);
			when(avgBidValue1.isNull()).thenReturn(false);
			when(avgBidValue1.getDoubleValue()).thenReturn(150000.5);
			FieldValueList row1 = FieldValueList.of(Arrays.asList(hourValue1, avgBidValue1), hourField, avgBidField);

			FieldValue hourValue2 = mock(FieldValue.class);
			when(hourValue2.getLongValue()).thenReturn(11L);
			FieldValue avgBidValue2 = mock(FieldValue.class);
			when(avgBidValue2.isNull()).thenReturn(false);
			when(avgBidValue2.getDoubleValue()).thenReturn(165000.0);
			FieldValueList row2 = FieldValueList.of(Arrays.asList(hourValue2, avgBidValue2), hourField, avgBidField);

			TableResult mockTableResult = mock(TableResult.class);
			when(mockTableResult.iterateAll()).thenReturn(Arrays.asList(row1, row2));

			Job mockJob = mock(Job.class);
			JobStatus mockJobStatus = mock(JobStatus.class);
			when(mockJobStatus.getError()).thenReturn(null);
			when(mockJob.waitFor()).thenReturn(mockJob);
			when(mockJob.getStatus()).thenReturn(mockJobStatus);
			when(mockJob.getQueryResults()).thenReturn(mockTableResult);

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when
			List<HourlyAverageBidResponse> result = bigQueryRepository.findHourlyAverageBid(category, days);

			// then
			assertThat(result).isNotNull();
			assertThat(result).hasSize(2);
			assertThat(result.get(0).getHourOfDay()).isEqualTo(10);
			assertThat(result.get(0).getAverageWinningBid()).isEqualTo(150001);
			assertThat(result.get(1).getHourOfDay()).isEqualTo(11);
			assertThat(result.get(1).getAverageWinningBid()).isEqualTo(165000);
		}

		@Test
		void findHourlyAverageBid_BigQuery_결과가_없을_경우_빈_리스트_반환() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			int days = 3;

			TableResult mockTableResult = mock(TableResult.class);
			when(mockTableResult.iterateAll()).thenReturn(Collections.emptyList()); // 빈 결과

			Job mockJob = mock(Job.class);
			JobStatus mockJobStatus = mock(JobStatus.class);
			when(mockJobStatus.getError()).thenReturn(null);
			when(mockJob.waitFor()).thenReturn(mockJob);
			when(mockJob.getStatus()).thenReturn(mockJobStatus);
			when(mockJob.getQueryResults()).thenReturn(mockTableResult);

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when
			List<HourlyAverageBidResponse> result = bigQueryRepository.findHourlyAverageBid(category, days);

			// then
			assertThat(result).isNotNull();
			assertThat(result).isEmpty();
		}

		@Test
		void findHourlyAverageBid_BigQuery_작업_대기_중_InterruptedException_발생() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			int days = 10;

			Job mockJob = mock(Job.class);
			when(mockJob.waitFor()).thenThrow(new InterruptedException("Test Interrupted"));

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when, then
			assertThatThrownBy(() -> bigQueryRepository.findHourlyAverageBid(category, days))
				.isInstanceOf(AnalyticsException.class)
				.hasFieldOrPropertyWithValue("errorCode", AnalyticsErrorCode.BQ_OPERATION_INTERRUPTED);
		}

		@Test
		void findHourlyAverageBid_BigQuery_작업_실행_실패() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			int days = 5;

			Job mockJob = mock(Job.class);
			JobStatus mockJobStatus = mock(JobStatus.class);
			BigQueryError bqError = new BigQueryError("reason", "location", "message");
			when(mockJobStatus.getError()).thenReturn(bqError);
			when(mockJob.waitFor()).thenReturn(mockJob);
			when(mockJob.getStatus()).thenReturn(mockJobStatus);

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when, then
			assertThatThrownBy(() -> bigQueryRepository.findHourlyAverageBid(category, days))
				.isInstanceOf(AnalyticsException.class)
				.hasFieldOrPropertyWithValue("errorCode", AnalyticsErrorCode.BQ_JOB_FAILED);
		}

		@Test
		void findHourlyAverageBid_BigQuery_클라이언트_create_호출_시_예외_발생() {
			// given
			ProductCategory category = ProductCategory.BEAUTY;
			int days = 1;

			given(mockBigQuery.create(any(JobInfo.class)));

			// when, then
			assertThatThrownBy(() -> bigQueryRepository.findHourlyAverageBid(category, days))
				.isInstanceOf(AnalyticsException.class)
				.hasFieldOrPropertyWithValue("errorCode", AnalyticsErrorCode.BQ_QUERY_EXECUTION_FAILED)
				.cause().isInstanceOf(RuntimeException.class);
		}
	}

	@Nested
	class FindDailyAverageBidByCategoryTests {

		@Test
		void findDailyAverageBidByCategory_BigQuery_조회_성공_및_결과_파싱_확인() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;

			Field dateField = Field.of("auction_date", StandardSQLTypeName.DATE);
			Field avgBidField = Field.of("average_winning_bid", StandardSQLTypeName.FLOAT64);

			FieldValue dateValue1 = mock(FieldValue.class);
			when(dateValue1.getStringValue()).thenReturn("2025-05-01");
			FieldValue avgBidValue1 = mock(FieldValue.class);
			when(avgBidValue1.isNull()).thenReturn(false);
			when(avgBidValue1.getDoubleValue()).thenReturn(50000.0);
			FieldValueList row1 = FieldValueList.of(Arrays.asList(dateValue1, avgBidValue1), dateField, avgBidField);

			FieldValue dateValue2 = mock(FieldValue.class);
			when(dateValue2.getStringValue()).thenReturn("2025-05-02");
			FieldValue avgBidValue2 = mock(FieldValue.class);
			when(avgBidValue2.isNull()).thenReturn(false);
			when(avgBidValue2.getDoubleValue()).thenReturn(55000.7);
			FieldValueList row2 = FieldValueList.of(Arrays.asList(dateValue2, avgBidValue2), dateField, avgBidField);

			TableResult mockTableResult = mock(TableResult.class);
			when(mockTableResult.iterateAll()).thenReturn(Arrays.asList(row1, row2));

			Job mockJob = mock(Job.class);
			JobStatus mockJobStatus = mock(JobStatus.class);
			when(mockJobStatus.getError()).thenReturn(null);
			when(mockJob.waitFor()).thenReturn(mockJob);
			when(mockJob.getStatus()).thenReturn(mockJobStatus);
			when(mockJob.getQueryResults()).thenReturn(mockTableResult);

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when
			List<DailyAverageBidResponse> result = bigQueryRepository.findDailyAverageBidByCategory(category);

			// then
			assertThat(result).isNotNull();
			assertThat(result).hasSize(2);
			assertThat(result.get(0).getAuctionDate()).isEqualTo(LocalDate.of(2025, 5, 1));
			assertThat(result.get(0).getAverageWinningBid()).isEqualTo(50000);
			assertThat(result.get(1).getAuctionDate()).isEqualTo(LocalDate.of(2025, 5, 2));
			assertThat(result.get(1).getAverageWinningBid()).isEqualTo(55001);
		}

		@Test
		void findDailyAverageBidByCategory_BigQuery_결과가_없을_경우_빈_리스트_반환() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;

			TableResult mockTableResult = mock(TableResult.class);
			when(mockTableResult.iterateAll()).thenReturn(Collections.emptyList());

			Job mockJob = mock(Job.class);
			JobStatus mockJobStatus = mock(JobStatus.class);
			when(mockJobStatus.getError()).thenReturn(null);
			when(mockJob.waitFor()).thenReturn(mockJob);
			when(mockJob.getStatus()).thenReturn(mockJobStatus);
			when(mockJob.getQueryResults()).thenReturn(mockTableResult);

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when
			List<DailyAverageBidResponse> result = bigQueryRepository.findDailyAverageBidByCategory(category);

			// then
			assertThat(result).isNotNull();
			assertThat(result).isEmpty();
		}

		@Test
		void findDailyAverageBidByCategory_BigQuery_작업_대기_중_InterruptedException_발생() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;

			Job mockJob = mock(Job.class);
			when(mockJob.waitFor()).thenThrow(new InterruptedException("Test Interrupted"));

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when, then
			assertThatThrownBy(() -> bigQueryRepository.findDailyAverageBidByCategory(category))
				.isInstanceOf(AnalyticsException.class)
				.hasFieldOrPropertyWithValue("errorCode", AnalyticsErrorCode.BQ_OPERATION_INTERRUPTED);
		}

		@Test
		void findDailyAverageBidByCategory_BigQuery_작업_실행_실패() throws InterruptedException {
			// given
			ProductCategory category = ProductCategory.CLOTHES;

			Job mockJob = mock(Job.class);
			JobStatus mockJobStatus = mock(JobStatus.class);
			BigQueryError bqError = new BigQueryError("reason", "location", "message");
			when(mockJobStatus.getError()).thenReturn(bqError);
			when(mockJob.waitFor()).thenReturn(mockJob);
			when(mockJob.getStatus()).thenReturn(mockJobStatus);

			given(mockBigQuery.create(any(JobInfo.class))).willReturn(mockJob);

			// when, then
			assertThatThrownBy(() -> bigQueryRepository.findDailyAverageBidByCategory(category))
				.isInstanceOf(AnalyticsException.class)
				.hasFieldOrPropertyWithValue("errorCode", AnalyticsErrorCode.BQ_JOB_FAILED);
		}

		@Test
		void findDailyAverageBidByCategory_BigQuery_클라이언트_create_호출_시_예외_발생() {
			// given
			ProductCategory category = ProductCategory.CLOTHES;
			given(mockBigQuery.create(any(JobInfo.class)));

			// when, then
			assertThatThrownBy(() -> bigQueryRepository.findDailyAverageBidByCategory(category))
				.isInstanceOf(AnalyticsException.class)
				.hasFieldOrPropertyWithValue("errorCode", AnalyticsErrorCode.BQ_QUERY_EXECUTION_FAILED)
				.cause().isInstanceOf(RuntimeException.class);
		}
	}
}
