package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.log.LogRepository;
import com.example.auctionmarket.common.log.LogService;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import com.example.auctionmarket.domain.coupon.exception.CouponErrorCode;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.user.repository.UserRepository;
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

import static java.time.LocalTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LogService logService;

    //admin - 쿠폰생성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CouponResponse createCoupon(AuthUser authUser, CouponRequest couponRequest) {
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            logService.saveLog(404L, "❌AUTHORITY_ERROR", String.valueOf(CouponErrorCode.NOT_ADMIN_AUTHORITY));
            //      log.error("❌권한 없음 - authUser={}, error={}", authUser.getAuthorities(), CouponErrorCode.NOT_ADMIN_AUTHORITY);
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }
        try {
            Coupon coupon = new Coupon(couponRequest.getCouponName(),
                    couponRequest.getDescription(),
                    couponRequest.getDiscountAmount(),
                    couponRequest.getExpiredAt(),
                    couponRequest.getAmount(),
                    couponRequest.getCouponType());

            Coupon savedCoupon = null;

            savedCoupon = couponRepository.save(coupon);
            logService.saveLog(savedCoupon.getId(), "✅COUPON_SAVE_SUCCESS", "쿠폰등록성공.");

            //쿠폰 생성과 동시에 redis에 추가
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiredAt = couponRequest.getExpiredAt();

            String redisKey = "coupon_stock:" + savedCoupon.getId();
            redisTemplate.opsForValue().set(redisKey, String.valueOf(couponRequest.getAmount()), Duration.between(now, expiredAt).getSeconds());

            return new CouponResponse(savedCoupon.getId(),
                    savedCoupon.getCouponName(),
                    savedCoupon.getDescription(),
                    savedCoupon.getDiscountAmount(),
                    savedCoupon.getExpiredAt(),
                    savedCoupon.getAmount());

        } catch (CouponException e) {
            log.error("❗로그 저장 실패: {}", e.getMessage());
            throw e; // 원래 예외 다시 던짐
        }
    }

    //쿠폰목록조회
    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {
        List<Coupon> coupons;
        try {
            coupons = couponRepository.findByCouponStatus(CouponStatus.VALID);//사용가능한 쿠폰만 조회
            logService.saveLog((long) coupons.size(), "✅COUPON_FIND_ALL_SUCCESS", "쿠폰리스트조회성공.");

            //    log.info("✅쿠폰 목록 조회 성공 ");
        } catch (Exception e) {
            logService.saveLog(404L, "❌COUPON_FIND_ALL_ERROR", e.getMessage());
            //     log.error("❌쿠폰 목록 조회 실패", e);
            throw e;
        }

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

        try {
            coupon = couponRepository.findById(couponId).orElseThrow(
                    () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));
            logService.saveLog((long) coupon.getId(), "✅COUPON_FIND_ID_SUCCESS", "해당쿠폰조회성공.");

            //    log.info("✅쿠폰 목록 조회 성공 - couponId={}", couponId);
        } catch (CouponException e) {
            logService.saveLog(404L, "❌COUPON_FIND_ID_ERROR", e.getMessage());
            //    log.error("❌쿠폰 목록 조회 실패", e);
            throw e;
        }

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
            logService.saveLog(404L, "❌AUTHORITY_ERROR", String.valueOf(CouponErrorCode.NOT_ADMIN_AUTHORITY));
            //   log.error("❌권한 없음 - authUser={}, error={}", authUser.getAuthorities(), CouponErrorCode.NOT_ADMIN_AUTHORITY);
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> {
                    logService.saveLog(404L, "❌COUPON_FIND_ID_ERROR", String.valueOf(CouponErrorCode.NOT_FOUND_COUPON));
                    //    log.error("❌쿠폰 없음 - couponId={}", couponId);
                    return new CouponException(CouponErrorCode.NOT_FOUND_COUPON);
                });

        try {
            coupon.update(couponUpdateRequest.getCouponName(),
                    couponUpdateRequest.getDescription(),
                    couponUpdateRequest.getDiscountAmount(),
                    couponUpdateRequest.getExpiredAt());

            logService.saveLog((long) coupon.getId(), "✅COUPON_UPDATE_INFO", "해당쿠폰수정성공");
        } catch (Exception e) {
            logService.saveLog(404L, "❌COUPON_UPDATE_ERROR", e.getMessage());
            //   log.error("❌쿠폰 수정 실패", e);

        }
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
            logService.saveLog(404L, "❌AUTHORITY_ERROR", String.valueOf(CouponErrorCode.NOT_ADMIN_AUTHORITY));
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> {
                    logService.saveLog(404L, "❌COUPON_FIND_ID_ERROR", String.valueOf(CouponErrorCode.NOT_FOUND_COUPON));
                    return new CouponException(CouponErrorCode.NOT_FOUND_COUPON);
                });
        try {
            coupon.expiredCoupon();
            logService.saveLog((long) coupon.getId(), "✅COUPON_DELETE_SUCCESS", "해당쿠폰삭제성공");
            //     log.info("✅쿠폰 삭제 성공 - couponId={}", couponId);
        } catch (Exception e) {
            logService.saveLog(404L, "❌COUPON_DELETE_ERROR", e.getMessage());
            //    log.error("❌쿠폰 삭제 실패", e);
        }
    }

}
