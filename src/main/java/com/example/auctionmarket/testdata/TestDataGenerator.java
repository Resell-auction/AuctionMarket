package com.example.auctionmarket.testdata;

import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Profile("dev") // "dev" 프로파일이 활성화될 때만 이 Bean을 생성하고 실행
@RequiredArgsConstructor
@Slf4j
public class TestDataGenerator {

	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final AuctionRepository auctionRepository;
	private final PasswordEncoder passwordEncoder;

	private static final int PEAK_HOUR_START = 14; // 피크 타임 시작
	private static final int PEAK_HOUR_END = 22;   // 피크 타임 끝
	private static final double PEAK_HOUR_MULTIPLIER_MAX_BONUS = 0.7; // 피크 타임 가격 상승률
	private static final double SEASONAL_MULTIPLIER_MAX_BONUS = 0.8; // 계절별 가격 상승률


	private final Faker faker = new Faker(new Locale("ko")); // 한국어 이름 생성
	private final Random random = new Random();

	@PostConstruct // Bean 이 생성되고 의존성 주입이 완료된 후 딱 한 번 실행됨
	@Transactional
	public void generateTestData() {
		// 데이터가 이미 있다면 생성 중지 => 중복 생성 방지
		if (userRepository.count() > 0) {
			log.info("테스트 데이터가 이미 있습니다: generateTestData 건너뜀");
			return;
		}
		log.info("테스트 데이터 생성 시작: generateTestData");

		// 유저 생성 1000명
		List<User> users = generateUsers(1000);
		List<User> savedUsers = userRepository.saveAll(users);
		log.info("유저 생성됨: {}", savedUsers.size());

		// 판매자 / 구매자 리스트 분리
		List<User> sellers = savedUsers.stream().filter(u -> u.getId() % 5 == 1).toList();
		List<User> buyers = savedUsers.stream().filter(u -> u.getId() % 5 != 1).toList();
		log.info("판매자 생성됨: {}", sellers.size());
		log.info("구매자 생성됨: {}", buyers.size());

		// 판매자 또는 구매자가 없으면 중단
		if (sellers.isEmpty() || buyers.isEmpty()) {
			log.error("판매자 / 구매자 생성 실패");
			return;
		}

		// 상품 생성 11000개
		List<Product> products = generateProducts(110000, sellers);
		List<Product> savedProducts = productRepository.saveAll(products);
		log.info("상품 생성됨: {}", savedProducts.size());

		if (savedProducts.isEmpty() || savedProducts.size() < 100000) {
			log.error("상품 생성 실패");
			return;
		}

		// 성공한 경매 데이터 생성 10000건
		List<Auction> auctions = generateSuccessfulAuctions(100000, savedProducts, buyers);
		auctionRepository.saveAll(auctions);
		log.info("성공한 경매 데이터 생성됨: {}", auctions.size());

		log.info("테스트 데이터 생성 완료");

	}

	// 유저 생성 메서드
	private List<User> generateUsers(int count) {
		List<User> userList = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			String email = faker.internet().safeEmailAddress() + i; // 중복 방지 위해 숫자 추가
			String encodedPassword = passwordEncoder.encode("password1234!");
			String nickname = faker.name().lastName() + faker.name().firstName() + i;
			String phoneNumber = faker.phoneNumber().cellPhone();
			Role role = Role.USER;
			User user = new User(email, encodedPassword, nickname, phoneNumber, role);
			userList.add(user);
		}
		return userList;
	}

	// 상품 생성 메서드
	private List<Product> generateProducts(int count, List<User> sellers) {
		List<Product> productList = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			User seller = sellers.get(random.nextInt(sellers.size())); // 판매자 랜덤 설정
			String productName = faker.commerce().productName();
			String productContent = faker.lorem().sentence();
			ProductCategory category = getRandomCategoryWithWeight(); // 가중치를 넣어도 카테고리 랜덤 설정
			Product product = new Product(seller, productName, productContent, category);
			productList.add(product);
		}
		return productList;
	}

	// 성공한 경매 생성 메서드
	private List<Auction> generateSuccessfulAuctions(int count, List<Product> products, List<User> buyers) {
		List<Auction> auctionList = new ArrayList<>();

		Collections.shuffle(products); // products 를 무작위로 섞어서 데이터 편향 완화

		for (int i = 0; i < count; i++) {
			Product product = products.get(i);
			User seller = product.getUser();
			User buyer;

			// 구매자와 판매자의 id가 같으면 안되기에 일단 뽑고 검사하는 do-while 문으로 반복 검사
			do {
				buyer = buyers.get(random.nextInt(buyers.size())); // 구매자 랜덤 설정
			} while (buyer.getId().equals(seller.getId())); // do 에서 뽑은 구매자와 상품의 판매자의 아이디가 같은지 확인

			long minPrice = generateMinPrice(product.getCategory());

			// 시간 생성 로직
			// 종료 시점 설정
			LocalDateTime endTime = generateRandomPastDateTime(365); // 365일 전까지의 임의 데이터 생성

			// 경매 지속 기간 설정
			long minDurationSeconds = TimeUnit.HOURS.toSeconds(1); // 최소 1시간 => 초로 환산
			long maxDurationSeconds = TimeUnit.DAYS.toSeconds(3); // 최대 3일 => 초로 환산
			long randomDurationSeconds = random.nextLong(minDurationSeconds, maxDurationSeconds + 1); // bound 는 미포함이기에 +1 추가
			Duration duration = Duration.ofSeconds(randomDurationSeconds);

			// 시작 시간 계산
			LocalDateTime startTime = endTime.minus(duration);

			// 낙찰가 가중치 설정
			long maxPrice = generateMaxPrice(minPrice, product.getCategory(), endTime);

			// 경매 생성
			Auction auction = new Auction();
			auction.setProduct(product);
			auction.setConsumerId(buyer.getId());
			auction.setMinPrice(minPrice);
			auction.setMaxPrice(maxPrice);
			auction.setStartTime(startTime);
			auction.setDuration(duration);
			auction.setEndTime(endTime);
			auction.setStatus(AuctionStatus.ENDED);

			auctionList.add(auction);
		}
		return auctionList;
	}

	// 카테고리 가중치 적용 생성 메서드
	private ProductCategory getRandomCategoryWithWeight() {
		int r = random.nextInt(100); // 기본 확률 100 으로 설정후 확률 분배
		if (r < 30) return ProductCategory.CLOTHES; // 30%
		else if (r < 55) return ProductCategory.SHOES; // 25%
		else if (r < 70) return ProductCategory.ACCESSORY; // 15%
		else if (r < 85) return ProductCategory.BEAUTY; // 15%
		else if (r < 95) return ProductCategory.BAG; // 15%
		else return ProductCategory.LUXURY; // 5%
	}

	// 카테고리별 최소 시작가 범위 설정
	private long generateMinPrice(ProductCategory category) {
		return switch (category) {
			case LUXURY -> random.nextLong(1000000, 2000000);
			case SHOES, BAG -> random.nextLong(20000, 50000);
			case CLOTHES, ACCESSORY -> random.nextLong(50000, 100000);
			case BEAUTY -> random.nextLong(30000, 60000);
		};
	}

	// 낙찰가 가중치 설정 메서드
	private long generateMaxPrice(long minPrice, ProductCategory category, LocalDateTime endTime) {

		// 카테고리별 낙찰가 가중치
		double baseMarkupRatio  = switch (category) {
			case LUXURY -> 1.1 + random.nextDouble(0.3); // 1.1 ~ 1.4 배 랜덤 인상
			case SHOES -> 1.2 + random.nextDouble(0.5); // 1.2 ~ 1.7 배 랜덤 인상
			case CLOTHES -> 1.3 + random.nextDouble(0.7); // 1.3 ~ 2.0 배 랜덤 인상
			default -> 1.1 + random.nextDouble(0.4); // 1.1 ~ 1.5 배 랜덤 인상
		};

		// 시간대별 낙찰가 가중치
		int hour = endTime.getHour();
		double timeMultiplier = 1.0; // 기본값

		if (hour >= PEAK_HOUR_START && hour < PEAK_HOUR_END) {
			// 피크 타임에 가중치 범위 안에서 랜덤 설정
			timeMultiplier += random.nextDouble(PEAK_HOUR_MULTIPLIER_MAX_BONUS);
		}

		// 계절별 낙찰가 가중치
		Month month = endTime.getMonth();
		double seasonalMultiplier = 1.0; // 기본값

		// 겨울(11 ~ 1월) => 의류 가격 상승
		if (List.of(Month.NOVEMBER, Month.DECEMBER, Month.JANUARY).contains(month)
			&& category == ProductCategory.CLOTHES) {
			seasonalMultiplier += random.nextDouble(SEASONAL_MULTIPLIER_MAX_BONUS);
		}

		// 여름(6 ~ 8월) => 화장품 가격 상승
		else if (List.of(Month.JUNE, Month.JULY, Month.AUGUST).contains(month)
			&& category == ProductCategory.BEAUTY) {
			seasonalMultiplier += random.nextDouble(SEASONAL_MULTIPLIER_MAX_BONUS);
		}

		// 장마철 (6 ~ 7월) => 신발 가격 상승
		else if (List.of(Month.JUNE, Month.JULY).contains(month)
			&& category == ProductCategory.SHOES) {
			seasonalMultiplier += random.nextDouble(SEASONAL_MULTIPLIER_MAX_BONUS * 0.7);
		}

		// 연말, 연초 (12 ~ 1월) => 럭셔리, 악세사리 가격 상승
		else if (List.of(Month.DECEMBER, Month.JANUARY).contains(month)
			&& category == ProductCategory.LUXURY) {
			seasonalMultiplier += random.nextDouble(SEASONAL_MULTIPLIER_MAX_BONUS * 0.5);
		}

		else if (List.of(Month.DECEMBER, Month.JANUARY).contains(month)
			&& category == ProductCategory.ACCESSORY) {
			seasonalMultiplier += random.nextDouble(SEASONAL_MULTIPLIER_MAX_BONUS * 0.5);
		}

		// 가중치 계산
		double finalMarkupRatio = baseMarkupRatio * timeMultiplier * seasonalMultiplier;
		long maxPrice = (long) (minPrice * finalMarkupRatio);
		long roundedMaxPrice = Math.round(maxPrice / 1000.0) * 1000;
		// Math.round(maxPrice / 1000.0) * 1000: 가격을 깔끔하게 만들기 위해 반올림함 => 1000원 단위로 만듬
		// 예: 12345678원 => 12346000원

		return Math.max(minPrice + 1000, roundedMaxPrice);
		//  Math.max(minPrice + 1000, ~): 만약 반올림된 값이 minPrice +1000 보다 낮다면 minPrice 값으로 반환 (특수한 경우 고려)
	}

	// 시간 생성 메서드
	private LocalDateTime generateRandomPastDateTime(int daysAgoMax) {
		long maxSecondsAgo = TimeUnit.DAYS.toSeconds(daysAgoMax);
		long minSecondsAgo = TimeUnit.HOURS.toSeconds(1);

		// 현재 시간에서 뺄 랜덤 초 생성 => 1시간 ~ daysAgoMax 일
		long secondsAgo = minSecondsAgo + random.nextLong(maxSecondsAgo - minSecondsAgo + 1);

		// 현재 시간에서 계산된 초만큼 빼서 과거 시간 생성
		return LocalDateTime.now().minusSeconds(secondsAgo);
	}
}