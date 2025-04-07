package com.example.auctionmarket.domain.coupon.controller;

import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coupons")
public class CouponController {

    private final CouponService couponService;

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
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(couponService.findById(id));
    }

    //수정
    @PutMapping("/{id}")//admin
    public ResponseEntity<CouponResponse> updateCoupon(@AuthenticationPrincipal AuthUser authUser,@PathVariable Long id, @RequestBody CouponUpdateRequest couponUpdateRequest){
        return ResponseEntity.ok(couponService.updateById(id, couponUpdateRequest));
    }

    //삭제
    @DeleteMapping("/{id}")//admin
    public void deleteCoupon(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long id){
        couponService.deleteById(id);
    }

    //유저에게 쿠폰을 원하는 수량만큼 주기
    @PostMapping("/{id}")
    public void giveCouponByUserId(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long id, @RequestBody CouponGiveRequest couponGiveRequest){
        giveCouponByUserId(id, couponGiveRequest);
    }
}
