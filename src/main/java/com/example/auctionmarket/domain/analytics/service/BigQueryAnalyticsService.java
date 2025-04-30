package com.example.auctionmarket.domain.analytics.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryParameterValue;
import com.google.cloud.bigquery.TableResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryAnalyticsService {

	private final BigQuery bigquery;

	private final String projectId;
	private final String datasetName;
	private final String tableName;

	// 프로퍼티 주입용 생성자
	public BigQueryAnalyticsService(
		BigQuery bigquery,
		@Value("${gcp.bigquery.project-id}") String projectId,
		@Value("${gcp.bigquery.dataset-name}") String datasetName,
		@Value("${gcp.bigquery.table-name}") String tableName) {
		this.bigquery = bigquery;
		this.projectId = projectId;
		this.datasetName = datasetName;
		this.tableName = tableName;
	}

	// 시간별 평균 낙찰가
	public List<HourlyAverageBidResponse> getHourlyAverageBid(HourlyAverageBidRequest request) throws InterruptedException {
		String category = String.valueOf(request.getCategory());
		int days = request.getDays();

		// BigQuery SQL 쿼리 설정
		String query = String.format(
			"SELECT " +
				"  EXTRACT(HOUR FROM auction_end_time) AS hour_of_day, " +
				"  AVG(max_price) AS average_winning_bid " +
				"FROM " +
				"  `%s.%s.%s` " + // 테이블 경로 포맷팅
				"WHERE " +
				"  product_category = @category " + // 파라미터 @category
				"  AND DATE(auction_end_time) >= DATE_SUB(CURRENT_DATE(), INTERVAL @days DAY) " + // 파라미터 @days
				"  AND DATE(auction_end_time) <= CURRENT_DATE() " +
				"GROUP BY " +
				"  hour_of_day " +
				"ORDER BY " +
				"  hour_of_day;",
			projectId, datasetName, tableName // 테이블 경로 삽입
		);

		log.info("getHourlyAverageBid: 파라미터: category = {}, days = {}", category, days);
		log.info("getHourlyAverageBid: BigQuery 쿼리문 = {}", query);

		// 쿼리 생성 + 파라미터 바인딩
		QueryJobConfiguration queryConfig =
			QueryJobConfiguration.newBuilder(query)
				.addNamedParameter("category", QueryParameterValue.string(category))
				.addNamedParameter("days", QueryParameterValue.int64(days))
				.setUseLegacySql(false)
				.build();

		// 쿼리 실행 요청 => 고유한 Job UUID 생성 => UUID 충돌 방지
		JobId jobId = JobId.of(UUID.randomUUID().toString());
		Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

		// 쿼리 완료 대기 => 동기
		queryJob = queryJob.waitFor();

		// 쿼리 실행 오류 확인
		if (queryJob.getStatus().getError() != null) {
			throw new RuntimeException("getHourlyAverageBid: BigQuery 작업 실패: " + queryJob.getStatus().getError().toString());
		}

		// 결과 가져오기
		TableResult result = queryJob.getQueryResults();
		List<HourlyAverageBidResponse> hourlyAverageBid = new ArrayList<>();

		// 결과 처리
		for (FieldValueList row : result.iterateAll()) {
			int hourOfDay = (int) row.get("hour_of_day").getLongValue();
			// average_winning_bid 널 처리
			double averageWinningBid = row.get("average_winning_bid").isNull() ? 0.0 : row.get("average_winning_bid").getDoubleValue();
			hourlyAverageBid.add(new HourlyAverageBidResponse(hourOfDay, averageWinningBid));
		}

		return hourlyAverageBid;
	}

	// 일자별 평균 낙찰가 (최근 1년)
	public List<DailyAverageBidResponse> getDailyAverageBidByCategory(DailyAverageBidRequest request) throws InterruptedException {
		String category = String.valueOf(request.getCategory());

		// BigQuery SQL 쿼리 설정
		String query = String.format(
			"SELECT " +
				"  DATE(auction_start_time) AS auction_date, " +
				"  AVG(max_price) AS average_winning_bid " +
				"FROM " +
				"  `%s.%s.%s` " +
				"WHERE " +
				"  product_category = @category " +
				"  AND DATE(auction_start_time) >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 YEAR) " +
				"  AND DATE(auction_start_time) <= CURRENT_DATE() " +
				"GROUP BY " +
				"  auction_date " +
				"ORDER BY " +
				"  auction_date;",
			projectId, datasetName, tableName
		);

		log.info("getDailyAverageBidByCategory: 파라미터: category = {}", category);
		log.info("getDailyAverageBidByCategory: BigQuery 쿼리문 = {}", query);

		// 쿼리 생성 + 파라미터 바인딩
		QueryJobConfiguration queryConfig =
			QueryJobConfiguration.newBuilder(query)
				.addNamedParameter("category", QueryParameterValue.string(category))
				.setUseLegacySql(false)
				.build();

		// 쿼리 실행 요청
		JobId jobId = JobId.of(UUID.randomUUID().toString());
		Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

		// 쿼리 완료 대기
		queryJob = queryJob.waitFor();

		// 쿼리 실행 오류 확인
		if (queryJob.getStatus().getError() != null) {
			throw new RuntimeException("getDailyAverageBidByCategory: BigQuery 작업 실패: " + queryJob.getStatus().getError().toString());
		}

		// 결과 가져오기
		TableResult result = queryJob.getQueryResults();
		List<DailyAverageBidResponse> dailyAverageBid = new ArrayList<>();

		// 결과 처리
		for (FieldValueList row : result.iterateAll()) {
			// BigQuery DATE 타입을 Java LocalDate 로 변환
			String dateString = row.get("auction_date").getStringValue();
			LocalDate auctionDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);

			// average_winning_bid 널 처리
			double averageWinningBid = row.get("average_winning_bid").isNull() ?
				0.0 : row.get("average_winning_bid").getDoubleValue();

			dailyAverageBid.add(new DailyAverageBidResponse(auctionDate, averageWinningBid));
		}

		return dailyAverageBid;
	}





}
