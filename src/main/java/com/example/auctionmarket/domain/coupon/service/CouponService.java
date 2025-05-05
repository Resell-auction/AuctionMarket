package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import com.example.auctionmarket.domain.coupon.exception.CouponErrorCode;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.time.LocalTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    //admin - 쿠폰생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CouponResponse createCoupon(AuthUser authUser, CouponRequest couponRequest) {

        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = new Coupon(couponRequest.getCouponName(),
                couponRequest.getDescription(),
                couponRequest.getDiscountAmount(),
                couponRequest.getExpiredAt(),
                couponRequest.getAmount(),
                couponRequest.getCouponType());

        Coupon savedCoupon = couponRepository.save(coupon);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = couponRequest.getExpiredAt();

        Duration duration = Duration.between(now, expiredAt);
        long expireSeconds = Math.max(duration.getSeconds(), 0);

        String redisKey = "coupon_stock:" + savedCoupon.getId();
        redisTemplate.opsForValue().set(redisKey, String.valueOf(couponRequest.getAmount()), expireSeconds, TimeUnit.SECONDS);

        return new CouponResponse(savedCoupon.getId(),
                savedCoupon.getCouponName(),
                savedCoupon.getDescription(),
                savedCoupon.getDiscountAmount(),
                savedCoupon.getExpiredAt(),
                savedCoupon.getAmount());
    }

    //쿠폰목록조회
    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {

        List<Coupon> coupons;

        coupons = couponRepository.findByCouponStatus(CouponStatus.VALID);//사용가능한 쿠폰만 조회

        List<CouponResponse> couponList = new ArrayList<>();

        for (Coupon coupon : coupons)
            couponList.add(new CouponResponse(coupon.getId(),
                    coupon.getCouponName(),
                    coupon.getDescription(),
                    coupon.getDiscountAmount(),
                    coupon.getExpiredAt(),
                    coupon.getAmount()));

        return couponList;
    }

    //쿠폰단건조회
    @Transactional(readOnly = true)
    public CouponResponse findById(Long couponId) {

        Coupon coupon;

        coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        return new CouponResponse(coupon.getId(),
                coupon.getCouponName(),
                coupon.getDescription(),
                coupon.getDiscountAmount(),
                coupon.getExpiredAt(),
                coupon.getAmount());
    }

    //admin- 쿠폰수정
    @Transactional
    public CouponResponse updateById(AuthUser authUser, Long couponId, CouponUpdateRequest couponUpdateRequest) {

        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> {
                    return new CouponException(CouponErrorCode.NOT_FOUND_COUPON);
                });

        coupon.update(couponUpdateRequest.getCouponName(),
                couponUpdateRequest.getDescription(),
                couponUpdateRequest.getDiscountAmount(),
                couponUpdateRequest.getExpiredAt());

        return new CouponResponse(coupon.getId(),
                coupon.getCouponName(),
                coupon.getDescription(),
                coupon.getDiscountAmount(),
                coupon.getExpiredAt(),
                coupon.getAmount());
    }

    //admin- 쿠폰삭제
    @Transactional
    public void deleteById(AuthUser authUser, Long couponId) {

        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> {
                    return new CouponException(CouponErrorCode.NOT_FOUND_COUPON);
                });
        coupon.expiredCoupon();
    }

    @Transactional
    public void expireCoupons() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1); // 어제 00:00:00

        int updatedCount = couponRepository.expireCouponsBetween(yesterday, now);

        log.info("만료된 쿠폰 수: {}", updatedCount);
    }

}
