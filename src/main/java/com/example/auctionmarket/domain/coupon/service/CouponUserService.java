package com.example.auctionmarket.domain.coupon.service;

import org.redisson.api.RedissonClient;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auctionmarket.common.aop.DistributedLock;
import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.exception.CouponErrorCode;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUserService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    //분산락+낙관적락
    @DistributedLock(key = "'coupon:' + #couponId")
    @Transactional
    @Retryable(
            value = {
                    ObjectOptimisticLockingFailureException.class,
                    CannotAcquireLockException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void giveCouponByUserId(AuthUser authUser, Long couponId, CouponGiveRequest couponGiveRequest) {
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));

        if (couponGiveRequest.getAmount() > 1) {
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);
        }

        CouponUser couponUser = CouponUser.builder()
            .users(users)
            .coupons(coupon)
            .build();
        couponUserRepository.save(couponUser);

        coupon.assignUniqueCoupon(); // 이게 version 충돌 원인
    }
}