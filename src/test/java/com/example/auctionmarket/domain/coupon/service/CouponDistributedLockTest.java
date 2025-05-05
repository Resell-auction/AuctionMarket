package com.example.auctionmarket.domain.coupon.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
class CouponDistributedLockTest {

    @Autowired
    private CouponUserService couponUserService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponUserRepository couponUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RedissonClient redissonClient;

    private Long couponId;
    private AuthUser adminUser;

    @BeforeEach
    void setup() {
        // 쿠폰 생성 (수량: 1)
        Coupon coupon = new Coupon("테스트쿠폰", "설명", 1L, LocalDateTime.now().plusDays(1), 1, CouponType.PERCENT);
        couponRepository.save(coupon);
        this.couponId = coupon.getId();

        // 관리자 유저
        adminUser = new AuthUser(1L, "admin@aaa.com", Role.ADMIN, "관리자");

        // 유저 100명 생성
        for (long i = 1; i <= 100; i++) {
            User user = new User(i+"email@email.com","password","nickname","phoneNum",Role.USER);
            userRepository.save(user);
        }

        couponUserRepository.deleteAllInBatch();

    }

    @Test
    void 동시에_100명이_쿠폰을_신청하면_1명만_성공한다() throws InterruptedException {

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (long i = 1; i <= threadCount; i++) {
            long userId = i;
            executor.submit(() -> {
                try {
                    CouponGiveRequest request = new CouponGiveRequest(userId, 1);
                    couponUserService.giveCouponByUserId(adminUser, couponId, request);
                } catch (Exception e) {
                    // 예외 무시 (중복 쿠폰 예외 발생 가능)
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<CouponUser> result = couponUserRepository.findAll();

        System.out.println("발급된 쿠폰 수: " + result.size());
        assertThat(result.size()).isEqualTo(1); // 딱 1명만 성공해야 함
    }
}
