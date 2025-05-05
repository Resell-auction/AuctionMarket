package com.example.auctionmarket.domain.coupon.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
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
@RequestMapping("/v1/admin/coupons")
public class CouponAdminController {

    private final CouponUserService couponUserService;
    private final CouponService couponService;

    //대량 쿠폰 발급(분산락+낙관적락)
    @PostMapping("/disoptimis/{couponId}")
    public ResponseEntity<String> giveBulkCoupons(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest) {

        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);

        return ResponseEntity.ok("쿠폰 발급 완료");
    }
}


