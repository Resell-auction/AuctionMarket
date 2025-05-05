package com.example.auctionmarket.domain.coupon.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coupons")
public class CouponController {

    private final CouponService couponService;
    private final CouponUserService couponUserService;

    //등록
    @PostMapping//admin만 가능
    public Response<CouponResponse> createCoupon(@AuthenticationPrincipal AuthUser authUser, @RequestBody CouponRequest couponRequest){

        return Response.of(couponService.createCoupon(authUser, couponRequest));
    }

    //전체 목록 조회
    @GetMapping
    public Response<List<CouponResponse>> findAll(){

        return Response.of(couponService.findAll());
    }

    //단건 조회
    @GetMapping("/{couponId}")
    public Response<CouponResponse> findById(@PathVariable("couponId") Long couponId){

        return Response.of(couponService.findById(couponId));
    }

    //수정
    @PutMapping("/{couponId}")//admin
    public Response<CouponResponse> updateCoupon(@AuthenticationPrincipal AuthUser authUser, @PathVariable("couponId") Long couponId, @RequestBody CouponUpdateRequest couponUpdateRequest){

        return Response.of(couponService.updateById(authUser, couponId, couponUpdateRequest));
    }

    //삭제
    @DeleteMapping("/{couponId}")//admin
    public Response<Void> deleteCoupon(@AuthenticationPrincipal AuthUser authUser, @PathVariable("couponId") Long couponId){

        couponService.deleteById(authUser, couponId);
        return Response.empty();
    }

    //유저에게 쿠폰을 원하는 수량만큼 주기
    @PutMapping("/{couponId}/give")
    public Response<Void> giveCouponByUserId(@AuthenticationPrincipal AuthUser authUser, @PathVariable("couponId") Long couponId, @RequestBody CouponGiveRequest couponGiveRequest){

        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);
        return Response.empty();
    }

    //쿠폰 만료
    @PostMapping("/expire")
    public Response<Void> expireCoupons() {

        couponService.expireCoupons();
        return Response.empty();
    }
}
