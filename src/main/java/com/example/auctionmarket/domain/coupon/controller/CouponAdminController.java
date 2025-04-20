package com.example.auctionmarket.domain.coupon.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.log.LogService;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/coupons/admin")
public class CouponAdminController {

    private final CouponUserService couponUserService;
    private final CouponService couponService;
    private final LogService logService;

    //대량 쿠폰 생성: 테스트용으로 삭제 예정
//    @PostMapping("/bulk/{count}")
//    public ResponseEntity<String> createBulkCoupons(@AuthenticationPrincipal AuthUser authUser, @PathVariable int count, @RequestBody CouponRequest couponRequest) {
//        for (int i = 0; i < count; i++) {
//            couponService.createCoupon(authUser, couponRequest);
//        }
//        return ResponseEntity.ok(count + "생산 완료");
//    }

    //대량 쿠폰 발급(락X)
    @PostMapping("/no-lock/{couponId}")
    public ResponseEntity<String> giveBulkCoupons(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest) {

      //  log.info("[쿠폰대량발급API]- 권한:", authUser.getAuthorities(), "쿠폰ID",couponId);
        logService.saveLog( couponId, "📍[API]COUPON_GIVE_BULK", "쿠폰대량발급API");

        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);

        return ResponseEntity.ok("쿠폰 발급 완료");
    }

    //대량 쿠폰 발급(낙관적락)
    @PostMapping("/optimistic-lock/{couponId}")
    public ResponseEntity<String> giveBulkCoupons2(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest) {

        logService.saveLog( couponId, "📍[API]COUPON_GIVE_BULK", "쿠폰대량발급API");

        couponUserService.giveCouponByUserId2(authUser, couponId, couponGiveRequest);

        return ResponseEntity.ok("쿠폰 발급 완료");
    }

    //대량 쿠폰 발급(비관적락)
    @PostMapping("/pessimistic-lock/{couponId}")
    public ResponseEntity<String> giveBulkCoupons3(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest) {

        logService.saveLog( couponId, "📍[API]COUPON_GIVE_BULK", "쿠폰대량발급API");

        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);

        return ResponseEntity.ok("쿠폰 발급 완료");
    }

    //대량 쿠폰 발급(분산락)
    @PostMapping("/distributed-lock/{couponId}")
    public ResponseEntity<String> giveBulkCoupons4(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest) {

        logService.saveLog( couponId, "📍[API]COUPON_GIVE_BULK", "쿠폰대량발급API");

        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);

        return ResponseEntity.ok("쿠폰 발급 완료");
    }

    //대량 쿠폰 발급(분산락+낙관적락)
    @PostMapping("/disoptimis/{couponId}")
    public ResponseEntity<String> giveBulkCoupons5(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest) {

        logService.saveLog( couponId, "📍[API]COUPON_GIVE_BULK", "쿠폰대량발급API");

        couponUserService.giveCouponByUserId3(authUser, couponId, couponGiveRequest);

        return ResponseEntity.ok("쿠폰 발급 완료");
    }
}

