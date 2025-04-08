package com.example.auctionmarket.domain.coupon.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.service.CouponService;
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

    //등록
    @PostMapping//admin만 가능
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CouponRequest couponRequest){
        return ResponseEntity.ok(couponService.createCoupon( couponRequest));
    }

    //전체 목록 조회
    @GetMapping
    public ResponseEntity<List<CouponResponse>> findAll(){
        return ResponseEntity.ok(couponService.findAll());
    }

    //단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(couponService.findById(id));
    }

    //수정
    @PutMapping("/{id}")//admin
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Long id, @RequestBody CouponUpdateRequest couponUpdateRequest){
        return ResponseEntity.ok(couponService.updateById(id, couponUpdateRequest));
    }

    //삭제
    @DeleteMapping("/{id}")//admin
    public void deleteCoupon(@PathVariable Long id){
        couponService.deleteById(id);
    }

    //유저에게 쿠폰을 원하는 수량만큼 주기
    @PutMapping("/admin/{id}")
    public void giveCouponByUserId(@PathVariable Long id, @RequestBody CouponGiveRequest couponGiveRequest){
        couponService.giveCouponByUserId( id, couponGiveRequest);
    }
}
