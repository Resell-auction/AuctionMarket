package com.example.auctionmarket.domain.analytics.service;

import com.example.auctionmarket.domain.analytics.dto.request.DailyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.request.HourlyAverageBidRequest;
import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 읽기 전용 트랜잭션

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MySQLAnalyticsService {

	@PersistenceContext
	private EntityManager entityManager; // Native Query 실행을 위해 EntityManager 사용

	/**
	 * 시간별 평균 낙찰가 조회 (MySQL)
	 * Auction 테이블과 Product 테이블을 조인하여 계산합니다.
	 * @param request 카테고리, 조회 기간(일)
	 * @return 시간대별 평균 낙찰가 리스트
	 */
	@Transactional(readOnly = true) // 데이터 변경이 없으므로 읽기 전용 트랜잭션 사용
	public List<HourlyAverageBidResponse> getHourlyAverageBid(HourlyAverageBidRequest request) {
		String category = String.valueOf(request.getCategory());
		int days = request.getDays();

		// Native SQL 쿼리 작성 (Auction과 Product 조인)
		// 참고: 실제 테이블 및 컬럼명은 프로젝트의 엔티티 정의와 일치해야 합니다.
		// *** 수정: a.status -> a.Auction_Status ***
		String sql = "SELECT " +
			"  HOUR(a.end_time) AS hour_of_day, " + // MySQL의 HOUR() 함수 사용
			"  AVG(a.max_price) AS average_winning_bid " +
			"FROM " +
			"  auctions a " + // Auction 테이블
			"JOIN " +
			"  product p ON a.product_id = p.id " + // Product 테이블과 조인
			"WHERE " +
			"  p.category = :category " + // Product 테이블의 카테고리 필터링
			"  AND a.end_time >= DATE_SUB(CURDATE(), INTERVAL :days DAY) " + // MySQL의 DATE_SUB(), CURDATE() 사용
			"  AND a.end_time <= NOW() " + // 현재 시간까지
			"  AND a.Auction_Status = 'ENDED' " + // *** 컬럼명 수정 *** 낙찰 완료된 경매만 대상 (필요시 조건 추가/변경)
			"GROUP BY " +
			"  hour_of_day " +
			"ORDER BY " +
			"  hour_of_day;";

		log.info("MySQL getHourlyAverageBid: 파라미터: category = {}, days = {}", category, days);
		log.info("MySQL getHourlyAverageBid: Native SQL = {}", sql);

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("category", category);
		query.setParameter("days", days);

		List<Object[]> results = query.getResultList();
		List<HourlyAverageBidResponse> hourlyAverageBids = new ArrayList<>();

		for (Object[] row : results) {
			// Native Query 결과에서 시간(hour_of_day) 추출
			// MySQL HOUR() 함수는 INT 타입을 반환하므로 Number로 캐스팅 후 intValue() 호출
			int hourOfDay = (row[0] != null) ? ((Number) row[0]).intValue() : 0;

			// Native Query 결과에서 평균 낙찰가(average_winning_bid) 추출
			// MySQL AVG() 함수는 DECIMAL 또는 DOUBLE 타입을 반환할 수 있음. Number로 캐스팅 후 doubleValue() 호출
			// 결과가 NULL일 경우 0.0으로 처리
			double averageWinningBid = (row[1] != null) ? ((Number) row[1]).doubleValue() : 0.0;
			hourlyAverageBids.add(new HourlyAverageBidResponse(hourOfDay, averageWinningBid));
		}

		return hourlyAverageBids;
	}

	/**
	 * 일자별 평균 낙찰가 조회 (MySQL - 최근 1년)
	 * Auction 테이블과 Product 테이블을 조인하여 계산합니다.
	 * @param request 카테고리
	 * @return 일자별 평균 낙찰가 리스트
	 */
	@Transactional(readOnly = true)
	public List<DailyAverageBidResponse> getDailyAverageBidByCategory(DailyAverageBidRequest request) {
		String category = String.valueOf(request.getCategory());

		// Native SQL 쿼리 작성 (Auction과 Product 조인)
		// *** 수정: a.status -> a.Auction_Status ***
		String sql = "SELECT " +
			"  DATE(a.end_time) AS auction_date, " + // MySQL의 DATE() 함수 사용
			"  AVG(a.max_price) AS average_winning_bid " +
			"FROM " +
			"  auctions a " +
			"JOIN " +
			"  product p ON a.product_id = p.id " +
			"WHERE " +
			"  p.category = :category " +
			"  AND a.end_time >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) " + // 최근 1년
			"  AND a.end_time <= NOW() " +
			"  AND a.Auction_Status = 'ENDED' " + // *** 컬럼명 수정 *** 낙찰 완료된 경매만 대상
			"GROUP BY " +
			"  auction_date " +
			"ORDER BY " +
			"  auction_date;";

		log.info("MySQL getDailyAverageBidByCategory: 파라미터: category = {}", category);
		log.info("MySQL getDailyAverageBidByCategory: Native SQL = {}", sql);

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("category", category);

		List<Object[]> results = query.getResultList();
		List<DailyAverageBidResponse> dailyAverageBids = new ArrayList<>();

		for (Object[] row : results) {
			// Native Query 결과에서 날짜(auction_date) 추출
			// MySQL DATE() 함수는 DATE 타입을 반환. java.sql.Date로 캐스팅 후 toLocalDate() 호출
			// 결과가 NULL일 경우 처리하지 않음
			LocalDate auctionDate = (row[0] != null) ? ((Date) row[0]).toLocalDate() : null;

			// Native Query 결과에서 평균 낙찰가(average_winning_bid) 추출
			// MySQL AVG() 함수는 DECIMAL 또는 DOUBLE 타입을 반환할 수 있음. Number로 캐스팅 후 doubleValue() 호출
			// 결과가 NULL일 경우 0.0으로 처리
			double averageWinningBid = (row[1] != null) ? ((Number) row[1]).doubleValue() : 0.0;

			// auctionDate가 null이 아닌 경우에만 리스트에 추가
			if (auctionDate != null) {
				dailyAverageBids.add(new DailyAverageBidResponse(auctionDate, averageWinningBid));
			}
		}

		return dailyAverageBids;
	}
}