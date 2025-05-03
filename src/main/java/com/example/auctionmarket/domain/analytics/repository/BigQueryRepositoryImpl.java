package com.example.auctionmarket.domain.analytics.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.exception.AnalyticsErrorCode;
import com.example.auctionmarket.domain.analytics.exception.AnalyticsException;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryParameterValue;
import com.google.cloud.bigquery.TableResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class BigQueryRepositoryImpl implements BigQueryRepository {

	private final BigQuery bigquery;
	private final String projectId;
	private final String datasetName;
	private final String tableName;
	private final String tableFullName;

	// 시간별 평균 낙찰가 조회 쿼리
	private static final String HOURLY_AVG_BID_QUERY =
		"SELECT " +
			"  EXTRACT(HOUR FROM auction_end_time) AS hour_of_day, " +
			"  AVG(max_price) AS average_winning_bid " +
			"FROM " +
			"  `%s` " +
			"WHERE " +
			"  product_category = @category " +
			"  AND DATE(auction_end_time) >= DATE_SUB(CURRENT_DATE(), INTERVAL @days DAY) " +
			"  AND DATE(auction_end_time) <= CURRENT_DATE() " +
			"GROUP BY " +
			"  hour_of_day " +
			"ORDER BY " +
			"  hour_of_day;";

	// 일자별 평균 낙찰가 조회 쿼리
	private static final String DAILY_AVG_BID_QUERY =
		"SELECT " +
			"  DATE(auction_start_time) AS auction_date, " +
			"  AVG(max_price) AS average_winning_bid " +
			"FROM " +
			"  `%s` " +
			"WHERE " +
			"  product_category = @category " +
			"  AND DATE(auction_start_time) >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 YEAR) " +
			"  AND DATE(auction_start_time) <= CURRENT_DATE() " +
			"GROUP BY " +
			"  auction_date " +
			"ORDER BY " +
			"  auction_date;";

	public BigQueryRepositoryImpl(
		BigQuery bigquery,
		@Value("${gcp.bigquery.project-id}") String projectId,
		@Value("${gcp.bigquery.dataset-name}") String datasetName,
		@Value("${gcp.bigquery.table-name}") String tableName) {
		this.bigquery = bigquery;
		this.projectId = projectId;
		this.datasetName = datasetName;
		this.tableName = tableName;
		this.tableFullName = String.format("%s.%s.%s", projectId, datasetName, tableName);
	}

	@Override
	public List<HourlyAverageBidResponse> findHourlyAverageBid(ProductCategory category, int days) {
		final String operationName = "findHourlyAverageBid";
		String categoryStr = String.valueOf(category);
		String query = String.format(HOURLY_AVG_BID_QUERY, tableFullName);

		QueryJobConfiguration queryConfig =
			QueryJobConfiguration.newBuilder(query)
				.addNamedParameter("category", QueryParameterValue.string(categoryStr))
				.addNamedParameter("days", QueryParameterValue.int64(days))
				.setUseLegacySql(false)
				.build();

		try {
			TableResult result = executeQuery(queryConfig, operationName);
			List<HourlyAverageBidResponse> hourlyAverageBid = new ArrayList<>();

			for (FieldValueList row : result.iterateAll()) {
				int hourOfDay = (int)row.get("hour_of_day").getLongValue();
				double averageWinningBidDouble =
					row.get("average_winning_bid").isNull() ?
						0.0 : row.get("average_winning_bid").getDoubleValue();
				int averageWinningBid = (int)Math.round(averageWinningBidDouble);
				hourlyAverageBid.add(new HourlyAverageBidResponse(hourOfDay, averageWinningBid));
			}
			return hourlyAverageBid;

		} catch (AnalyticsException e) {
			throw e;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("인터럽트 발생: operationName = {}, category = {}, days = {}", operationName, category, days, e);
			throw new AnalyticsException(AnalyticsErrorCode.BQ_OPERATION_INTERRUPTED, e);
		} catch (Exception e) {
			log.error("예상치 못한 오류 발생: operationName = {}, category = {}, days = {}", operationName, category, days, e);
			throw new AnalyticsException(AnalyticsErrorCode.BQ_QUERY_EXECUTION_FAILED, e);
		}
	}

	@Override
	public List<DailyAverageBidResponse> findDailyAverageBidByCategory(ProductCategory category) {
		final String operationName = "findDailyAverageBidByCategory";
		String categoryStr = String.valueOf(category);
		String query = String.format(DAILY_AVG_BID_QUERY, tableFullName);

		QueryJobConfiguration queryConfig =
			QueryJobConfiguration.newBuilder(query)
				.addNamedParameter("category", QueryParameterValue.string(categoryStr))
				.setUseLegacySql(false)
				.build();

		try {
			TableResult result = executeQuery(queryConfig, operationName);
			List<DailyAverageBidResponse> dailyAverageBid = new ArrayList<>();

			for (FieldValueList row : result.iterateAll()) {
				String dateString = row.get("auction_date").getStringValue();
				LocalDate auctionDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
				double averageWinningBidDouble =
					row.get("average_winning_bid").isNull() ?
						0.0 : row.get("average_winning_bid").getDoubleValue();
				int averageWinningBid = (int)Math.round(averageWinningBidDouble);
				dailyAverageBid.add(new DailyAverageBidResponse(auctionDate, averageWinningBid));
			}
			return dailyAverageBid;

		} catch (AnalyticsException e) {
			throw e;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("인터럽트 발생: operationName = {}, category = {}", operationName, category, e);
			throw new AnalyticsException(AnalyticsErrorCode.BQ_OPERATION_INTERRUPTED, e);
		} catch (Exception e) {
			log.error("예상치 못한 오류 발생: operationName = {}, category = {}", operationName, category, e);
			throw new AnalyticsException(AnalyticsErrorCode.BQ_QUERY_EXECUTION_FAILED, e);
		}
	}

	private TableResult executeQuery(QueryJobConfiguration queryConfig, String operationName) throws InterruptedException {
		JobId jobId = JobId.of(UUID.randomUUID().toString());
		Job queryJob = null;

		try {
			queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());
			queryJob = queryJob.waitFor();

			if (queryJob == null) {
				log.error("BigQuery Job 객체가 null: operationName = {}, jobId = {}", operationName, jobId);
				throw new AnalyticsException(AnalyticsErrorCode.BQ_QUERY_EXECUTION_FAILED);
			}

			if (queryJob.getStatus() == null) {
				log.error("BigQuery Job Status 가 null: operationName = {}, jobId: {}", operationName, jobId);
				throw new AnalyticsException(AnalyticsErrorCode.BQ_QUERY_EXECUTION_FAILED);
			}

			if (queryJob.getStatus().getError() != null) {
				BigQueryError error = queryJob.getStatus().getError();
				log.error("BigQuery 작업 실패: operationName = {}, jobId = {}. 이유: {}, 위치: {}, 메시지: {}",
					operationName, jobId, error.getReason(), error.getLocation(), error.getMessage());
				throw new AnalyticsException(AnalyticsErrorCode.BQ_JOB_FAILED, new RuntimeException(error.toString()));
			}

			return queryJob.getQueryResults();

		} catch (AnalyticsException ae) {
			log.error("AnalyticsException 발생: operationName = {}, jobId = {}. errorCode = {}",
				operationName, jobId, ae.getErrorCode(), ae);
			throw ae;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("BigQuery 작업 대기 중 인터럽트 발생: operationName = {}, jobId: {}", operationName, jobId, e);
			throw new AnalyticsException(AnalyticsErrorCode.BQ_OPERATION_INTERRUPTED, e);
		} catch (Exception e) {
			log.error("BigQuery 쿼리 실행 중 예상치 못한 오류 발생: operationName = {}, jobId: {}. 오류: {}",
				operationName, jobId, e.getMessage(), e);
			throw new AnalyticsException(AnalyticsErrorCode.BQ_QUERY_EXECUTION_FAILED, e);
		}
	}
}