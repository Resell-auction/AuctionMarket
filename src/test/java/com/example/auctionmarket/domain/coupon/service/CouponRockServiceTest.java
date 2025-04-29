//package com.example.auctionmarket.coupons;
//
//import com.example.auctionmarket.common.auth.AuthUser;
//import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
//import com.example.auctionmarket.domain.coupon.entity.Coupon;
//import com.example.auctionmarket.domain.coupon.enums.CouponType;
//import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
//import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
//import com.example.auctionmarket.domain.coupon.service.CouponUserService;
//import com.example.auctionmarket.domain.user.entity.User;
//import com.example.auctionmarket.domain.user.enums.Role;
//import com.example.auctionmarket.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.Authentication;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//public class CouponRockServiceTest {
//
//    @Autowired
//    private CouponUserService couponUserService;
//
//    @Autowired
//    private CouponUserRepository couponUserRepository;
//
//    @Autowired
//    private CouponRepository couponRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private Authentication authUser;
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    @Test
//    void redis_분산락_쿠폰_동시_발급_테스트() throws InterruptedException {
//        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
//        CouponGiveRequest couponGiveRequest= new CouponGiveRequest(1L,4);
//        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");
//        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,10, CouponType.PERCENT);
//        User user = Mockito.mock(User.class);
//
//        int threadCount = 50;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            final Long userId = Long.valueOf(i);
//            executorService.execute(() -> {
//                try {
//                    couponUserService.giveBulkCoupons(authUser, 1L, couponGiveRequest);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        long issuedCount = couponRepository.count();
//        assertEquals(1000, issuedCount);
//    }
//
//}
//
//
//
//    @BeforeEach
//    void setup() {
//        Coupon coupon = new Coupon();
//        coupon.setAmount(100);
//        couponRepository.save(coupon);
//    }
//
//    @Test
//    void 동시에_100명_쿠폰_발급() throws InterruptedException {
//        int threadCount = 100;
//        ExecutorService executor = Executors.newFixedThreadPool(16);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        Long couponId = couponRepository.findAll().get(0).getId();
//
//        for (int i = 0; i < threadCount; i++) {
//            final long userId = i;
//            executor.submit(() -> {
//                try {
//                    couponUserService.(userId, couponId);
//                } catch (Exception ignored) {} finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        Coupon coupon = couponRepository.findById(couponId).get();
//        System.out.println("최종 남은 수량: " + coupon.getQuantity());
//        Assertions.assertTrue(coupon.getQuantity() >= 0);
//    }
//}