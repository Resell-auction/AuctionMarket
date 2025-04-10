package com.example.auctionmarket.domain.coupon.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coupons")
public class CouponController {

    private final CouponService couponService;
    private final CouponUserService couponUserService;

    //등록
    @PostMapping//admin만 가능
    public ResponseEntity<CouponResponse> createCoupon(@AuthenticationPrincipal AuthUser authUser, @RequestBody CouponRequest couponRequest){
        return ResponseEntity.ok(couponService.createCoupon(authUser, couponRequest));
    }

    //전체 목록 조회
    @GetMapping
    public ResponseEntity<List<CouponResponse>> findAll(){
        return ResponseEntity.ok(couponService.findAll());
    }

    //단건 조회
    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponse> findById(@PathVariable Long couponId){
        return ResponseEntity.ok(couponService.findById(couponId));
    }

    //수정
    @PutMapping("/{couponId}")//admin
    public ResponseEntity<CouponResponse> updateCoupon(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponUpdateRequest couponUpdateRequest){
        return ResponseEntity.ok(couponService.updateById(authUser, couponId, couponUpdateRequest));
    }

    //삭제
    @DeleteMapping("/{couponId}")//admin
    public void deleteCoupon(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId){
        couponService.deleteById(authUser, couponId);
    }

    //유저에게 쿠폰을 원하는 수량만큼 주기
    @PutMapping("/{couponId}/give")
    public void giveCouponByUserId(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long couponId, @RequestBody CouponGiveRequest couponGiveRequest){
        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);
    }
}
