package com.example.auctionmarket.domain.coupon.service;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.auctionmarket.AuctionMarketApplication;
import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.user.enums.Role;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
@SpringBootTest(classes = AuctionMarketApplication.class)
public class CouponLockStrategyPerformanceTest {

    @Autowired
    CouponUserService couponUserService;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    private EntityManager entityManager;

    private static final int THREAD_COUNT = 50;

    private Long couponId; // 클래스 필드로 선언


    @BeforeEach
    void setupTestCoupon() {
        Coupon coupon = new Coupon("쿠폰이름", "설명", 10L, LocalDateTime.now().plusDays(1), 100, CouponType.PERCENT);
     //   coupon.setAmount(999);
        couponRepository.save(coupon);
        couponRepository.flush(); // 💡 트랜잭션 commit 강제
        entityManager.clear();    // 💡 영속성 컨텍스트 초기화 (옵션)

        this.couponId = coupon.getId(); // 필드에 저장!
    }

    @Test
    void 락_전략별_성능_비교() throws Exception {
        //    Long couponId = 1L;
        Long userId = 999L;
        AuthUser admin = new AuthUser(1L, "abc@gmail.com" , Role.ADMIN,"nickname");

        // 락 전략별로 테스트
//        measure("No Lock", () -> couponUserService.giveCouponByUserId(admin, couponId, new CouponGiveRequest(userId, 1)));
//        measure("Optimistic Lock", () -> couponUserService.giveCouponByUserId2(admin, couponId, new CouponGiveRequest(userId, 1)));
//        measure("Pessimistic Lock", () -> couponUserService.giveCouponByUserId4(admin, couponId, new CouponGiveRequest(userId, 1)));
//        measure("Distributed + Optimistic", () -> couponUserService.giveCouponByUserId5(admin, couponId, new CouponGiveRequest(userId, 1)));
    }

    private void measure(String label, Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        long start = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(() -> {
                try {
                    task.run();
                    success.incrementAndGet();
                } catch (Exception e) {
                    //     e.printStackTrace();
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        executor.shutdown();

        System.out.printf("[%s] 성공: %d, 실패: %d, 총 시간: %dms, 평균 요청: %.2fms\n",
                label,
                success.get(),
                fail.get(),
                (end - start),
                (end - start) / (double) THREAD_COUNT
        );
    }

}

