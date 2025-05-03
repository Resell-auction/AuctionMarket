//
//package com.example.auctionmarket.domain.coupon.service;
//
//import com.example.auctionmarket.AuctionMarketApplication;
//import com.example.auctionmarket.common.auth.AuthUser;
//import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
//import com.example.auctionmarket.domain.coupon.entity.Coupon;
//import com.example.auctionmarket.domain.coupon.entity.CouponUser;
//import com.example.auctionmarket.domain.coupon.enums.CouponType;
//import com.example.auctionmarket.domain.coupon.exception.CouponException;
//import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
//import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
//import com.example.auctionmarket.domain.user.entity.User;
//import com.example.auctionmarket.domain.user.enums.Role;
//import com.example.auctionmarket.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.dao.OptimisticLockingFailureException;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
////import org.testcontainers.containers.GenericContainer;
////import org.testcontainers.containers.MySQLContainer;
////import org.testcontainers.junit.jupiter.Container;
////import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(classes = AuctionMarketApplication.class)
////@Testcontainers
//class CouponLockServiceTest {
//
////    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
////            "postgres:16-alpine"
////    );
//
//
//    @Autowired
//    private CouponUserService couponUserService;
//
//    @MockBean
//    private CouponRepository couponRepository;
//
//    @MockBean
//    private CouponUserRepository couponUserRepository;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @MockBean
//    private RedissonClient redissonClient;
//
//    @MockBean
//    private RLock lock;
//
////    @MockBean
////    private BigQuery bigQuery;
////
////    @MockBean
////    private RestTemplate restTemplate;
//
//    private AuthUser adminUser;
//    private Coupon coupon;
//    private User targetUser;
//
//    @BeforeEach
//    void setup() {
//        LocalDateTime expiredAt = LocalDateTime.now();
//        adminUser = new AuthUser(1L, "abc@gmail.com", Role.ADMIN, "nickname");
//
//        coupon = new Coupon("이름", "설명", (long) 1.0, expiredAt, 100, CouponType.PERCENT);
//        coupon.setId(100L);
//        coupon.setVersion(0L); // for optimistic locking
//
//        targetUser = new User();
//        targetUser.setId(200L);
//
//        when(redissonClient.getLock((String) any())).thenReturn(lock);
//    }
////
////    @Container
////    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
////            .withDatabaseName("testdb")
////            .withUsername("testuser")
////            .withPassword("testpass");
////
////    @Container
////    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
////            .withExposedPorts(6379);
////
////    @DynamicPropertySource
////    static void configureProperties(DynamicPropertyRegistry registry) {
////        registry.add("spring.datasource.url", mysql::getJdbcUrl);
////        registry.add("spring.datasource.username", mysql::getUsername);
////        registry.add("spring.datasource.password", mysql::getPassword);
////
////        registry.add("spring.data.redis.host", redis::getHost);
////        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
////
////    }
//
//    @Test
//    void 정상_쿠폰_발급() throws Exception {
//        CouponGiveRequest request = new CouponGiveRequest(targetUser.getId(), 1);
//
//        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
//        when(couponRepository.findById(100L)).thenReturn(Optional.of(coupon));
//        when(userRepository.findById(200L)).thenReturn(Optional.of(targetUser));
//
//        couponUserService.giveCouponByUserId5(adminUser, 100L, request);
//
//        verify(couponUserRepository).save(any(CouponUser.class));
//    }
//
//    @Test
//    void 권한_없는_유저_예외() {
//        AuthUser normalUser = new AuthUser(1L, "abc@gmail.com", Role.USER, "nickname");
//
//        CouponGiveRequest request = new CouponGiveRequest(200L, 1);
//
//        assertThrows(CouponException.class, () ->
//                couponUserService.giveCouponByUserId5(normalUser, 100L, request));
//    }
//
//    @Test
//    void 쿠폰_없음_예외() throws Exception {
//        CouponGiveRequest request = new CouponGiveRequest(200L, 1);
//
//        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
//        when(couponRepository.findById(100L)).thenReturn(Optional.empty());
//
//        assertThrows(CouponException.class, () ->
//                couponUserService.giveCouponByUserId5(adminUser, 100L, request));
//    }
//
//    @Test
//    void 유저_없음_예외() throws Exception {
//        CouponGiveRequest request = new CouponGiveRequest(999L, 1);
//
//        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
//        when(couponRepository.findById(100L)).thenReturn(Optional.of(coupon));
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThrows(CouponException.class, () ->
//                couponUserService.giveCouponByUserId5(adminUser, 100L, request));
//    }
//
//    @Test
//    void 중복_쿠폰_예외() throws Exception {
//        CouponGiveRequest request = new CouponGiveRequest(200L, 2);
//
//        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
//        when(couponRepository.findById(100L)).thenReturn(Optional.of(coupon));
//        when(userRepository.findById(200L)).thenReturn(Optional.of(targetUser));
//
//        assertThrows(CouponException.class, () ->
//                couponUserService.giveCouponByUserId5(adminUser, 100L, request));
//    }
//
//    @Test
//    void 분산락_중복_차단_테스트() throws InterruptedException {
//        CouponGiveRequest request = new CouponGiveRequest(200L, 1);
//
//        when(redissonClient.getLock((String) any())).thenReturn(lock);
//        when(lock.tryLock(anyLong(), anyLong(), any()))
//                .thenReturn(true)  // 첫 요청 성공
//                .thenReturn(false); // 두 번째 실패
//
//        when(couponRepository.findById(100L)).thenReturn(Optional.of(coupon));
//        when(userRepository.findById(200L)).thenReturn(Optional.of(targetUser));
//
//        Runnable task1 = () -> couponUserService.giveCouponByUserId5(adminUser, 100L, request);
//        Runnable task2 = () -> {
//            assertThrows(RuntimeException.class, () ->
//                    couponUserService.giveCouponByUserId5(adminUser, 100L, request));
//        };
//
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        executor.submit(task1);
//        executor.submit(task2);
//
//        executor.shutdown();
//        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
//    }
//
//    @Test
//    void 낙관적락_충돌_테스트() throws InterruptedException {
//        Coupon sameCoupon1 = new Coupon();
//        sameCoupon1.setId(100L);
//        sameCoupon1.setVersion(0L);
//
//        Coupon sameCoupon2 = new Coupon();
//        sameCoupon2.setId(100L);
//        sameCoupon2.setVersion(0L);
////같은 아이디와 버전을 가진 쿠폰 객체 2개 준비
//        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
//        //분산락 성공
//        when(couponRepository.findById(100L)).thenReturn(Optional.of(sameCoupon1));
//        when(userRepository.findById(200L)).thenReturn(Optional.of(targetUser));
////쿠폰,유저 존재유무 성공
//        doThrow(OptimisticLockingFailureException.class)
//                .when(couponUserRepository).save(any());
////낙관적락 충돌 가정
//        CouponGiveRequest request = new CouponGiveRequest(200L, 1);
//
//        assertThrows(OptimisticLockingFailureException.class, () ->
//                couponUserService.giveCouponByUserId5(adminUser, 100L, request));
//    }
//}
