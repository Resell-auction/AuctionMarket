package com.example.auctionmarket.domain.coupon.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/coupons")
public class CouponAdminController {

    private final CouponUserService couponUserService;

    //대량 쿠폰 발급(분산락+낙관적락)
    @PostMapping("/disoptimis/{couponId}")
    public Response<Void> giveBulkCoupons(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest) {

        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);

		return Response.empty();
	}
}


