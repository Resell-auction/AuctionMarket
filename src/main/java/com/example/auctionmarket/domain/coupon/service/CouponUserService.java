package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.aop.DistributedLock;
import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.log.LogService;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.exception.CouponErrorCode;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.auctionmarket.common.redis.RedissonConfig;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUserService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LogService logService;


    @Retryable(
            value = {CannotAcquireLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200)
    )

    //기본.락이 없을 때.
    @Transactional
    public void giveCouponByUserId(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest) {
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }


        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));//user는 쿠폰 리스트를 갖고 있음.

        if (couponGiveRequest.getAmount() > 1)
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);

        //user, coupon객체에 couponuser 객체 생성 후 저장.
        CouponUser couponUser = new CouponUser();
        couponUser.setUsers(users);
        couponUser.setCoupons(coupon);
        couponUserRepository.save(couponUser);

        coupon.assignUniqueCoupon();

        coupon.discountCoupon(couponGiveRequest.getAmount());

    }

    //낙관적락
    @Transactional
    public void giveCouponByUserId2(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest) {
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));//user는 쿠폰 리스트를 갖고 있음.

        if (couponGiveRequest.getAmount() > 1)
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);

        //user, coupon객체에 couponuser 객체 생성 후 저장.
        CouponUser couponUser = new CouponUser();
        couponUser.setUsers(users);
        couponUser.setCoupons(coupon);
        couponUserRepository.save(couponUser);

        coupon.assignUniqueCoupon();

        coupon.discountCoupon(couponGiveRequest.getAmount());
    }

    //낙관적락+분산락
    @DistributedLock(key = "#couponGiveRequest.userId")
    @Transactional
    public void giveCouponByUserId3(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest) {
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            logService.saveLog(403L, "❌ERROR", "권한이 없습니다.");
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> {
                    logService.saveLog(404L, "❌ERROR", "해당 쿠폰 조회를 실패했습니다.");

                    return new CouponException(CouponErrorCode.NOT_FOUND_COUPON);
                }
        );

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> {
                    logService.saveLog(404L, "❌ERROR", "해당 유저 조회를 실패했습니다.");

                    return new CouponException(CouponErrorCode.NOT_FOUND_USER);
                });//user는 쿠폰 리스트를 갖고 있음.

        if (couponGiveRequest.getAmount() > 1){
            logService.saveLog(404L, "❌ERROR", "쿠폰은 1장만 발급 가능합니다.");

            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);
        }

        //user, coupon객체에 couponuser 객체 생성 후 저장.
        CouponUser couponUser = new CouponUser();
        couponUser.setUsers(users);
        couponUser.setCoupons(coupon);
        couponUserRepository.save(couponUser);

        coupon.assignUniqueCoupon();

        String redisKey = "coupon_stock:" + id;
        Long remains = redisTemplate.opsForValue().decrement(redisKey);
        if (remains == null || remains < 0) {
            // *옵션* DB 롤백이나 예외 처리
            throw new IllegalStateException("Redis stock underflow");
        }

        logService.saveLog((long) coupon.getId(), "✅INFO", "해당 쿠폰 수정을 성공했습니다.");
     //   log.info("💾 쿠폰 저장 시도: coupon={}, version={}", coupon.getId(), coupon.getVersion());
    }
//
//    //분산락으로 쿠폰 한 장씩 발급
//    @DistributedLock(key = "#userId")
//    @Transactional
//    public void giveBulkCoupons(AuthUser authUser,Long couponId, Long userId){
//        RLock lock = redissonClient.getLock("coupon_lock");
//
//        try {
//            if (lock.tryLock(3, 1, TimeUnit.SECONDS)) { // 3초 기다리고 1초 안에 처리
//                try {
//                    // 1. 유저 중복 검사
//                    if (redisTemplate.hasKey("user:" + authUser.getId() + ":coupon_issued")) {
//                        throw new CouponException(CouponErrorCode.ISSUED_COUPON);
//                    }
//
//                    // 2. 재고 검사 (atomic decrement)
//                    Long stock = redisTemplate.opsForValue().decrement("coupon_stock" + couponId);
//                    if (stock == null || stock < 0) {
//                        throw new CouponException(CouponErrorCode.OUT_OF_COUPON);
//                    }
//
//                    // 3. 쿠폰 발급 처리 (DB 저장 + Redis 유저 발급 플래그)
//                    giveCouponByUserId(authUser, couponId, couponGiveRequest);
//                    redisTemplate.opsForValue().set("user:" + authUser.getId() + ":coupon_issued", "1");
//
//                } finally {
//                    lock.unlock();
//                }
//
//            } else {
//                throw new LockTimeoutException();
//            }
//
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    @Transactional
//    public void issueCoupon(Long userId, Long couponId){
//
//        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
//                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));
//
//        User users = userRepository.findById(userId).orElseThrow(
//                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));//user는 쿠폰 리스트를 갖고 있음.
//
//        CouponUser couponUser = new CouponUser();
//        couponUser.setUsers(users);
//        couponUser.setCoupons(coupon);
//        couponUserRepository.save(couponUser);
//    }

}
