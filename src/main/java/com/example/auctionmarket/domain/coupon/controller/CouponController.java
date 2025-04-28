package com.example.auctionmarket.domain.coupon.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.log.LogService;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.exception.CouponErrorCode;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.service.CouponService;
import com.example.auctionmarket.domain.coupon.service.CouponUserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/coupons")
public class CouponController {

    private final CouponService couponService;
    private final CouponUserService couponUserService;
    private final LogService logService;

    //등록
    @PostMapping//admin만 가능
    public ResponseEntity<CouponResponse> createCoupon(@AuthenticationPrincipal AuthUser authUser, @RequestBody CouponRequest couponRequest){

//        if (authUser.getAuthorities() == null) {
//            logService.saveLog(404L, "❌AUTHORITY_ERROR", String.valueOf(CouponErrorCode.NOT_ADMIN_AUTHORITY));
//            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
//        }
     //   log.info("[쿠폰생성API] - 권한: {}", authUser.getAuthorities());
//        logService.saveLog( authUser.getId(), "📍[API]COUPON_CREATE", "쿠폰생성API");

        return ResponseEntity.ok(couponService.createCoupon(authUser, couponRequest));
    }

    //전체 목록 조회
    @GetMapping
    public ResponseEntity<List<CouponResponse>> findAll(){

   //     log.info("[쿠폰목록조회API]");
        logService.saveLog( 200L, "📍[API]COUPON_FIND_ALL", "쿠폰목록조회API");

        return ResponseEntity.ok(couponService.findAll());
    }

    //단건 조회
    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponse> findById(@PathVariable("couponId") Long couponId){

  //      log.info("[쿠폰단건조회API]- 쿠폰ID: {}", couponId);
        logService.saveLog( couponId, "📍[API]COUPON_FIND_ID", "쿠폰목록조회API");

        return ResponseEntity.ok(couponService.findById(couponId));
    }

    //수정
    @PutMapping("/{couponId}")//admin
    public ResponseEntity<CouponResponse> updateCoupon(@AuthenticationPrincipal AuthUser authUser, @PathVariable("couponId") Long couponId, @RequestBody CouponUpdateRequest couponUpdateRequest){

   //     log.info("[쿠폰수정API]- 권한: {} , 쿠폰ID: {}", authUser.getAuthorities(), couponId);
        logService.saveLog( couponId, "📍[API]COUPON_UPDATE_ID", "쿠폰목록조회API");

        return ResponseEntity.ok(couponService.updateById(authUser, couponId, couponUpdateRequest));
    }

    //삭제
    @DeleteMapping("/{couponId}")//admin
    public void deleteCoupon(@AuthenticationPrincipal AuthUser authUser, @PathVariable("couponId") Long couponId){

 //      log.info("[쿠폰삭제API]- 권한: {} , 쿠폰ID: {}", authUser.getAuthorities(), couponId);
        logService.saveLog( couponId, "📍[API]COUPON_DELETE_ID", "쿠폰삭제API");

        couponService.deleteById(authUser, couponId);
    }

    //유저에게 쿠폰을 원하는 수량만큼 주기
    @PutMapping("/{couponId}/give")
    public void giveCouponByUserId(@AuthenticationPrincipal AuthUser authUser, @PathVariable("couponId") Long couponId, @RequestBody CouponGiveRequest couponGiveRequest){

    //    log.info("[쿠폰발급API]- 권한: {} , 쿠폰ID: {}", authUser.getAuthorities(), couponId);
        logService.saveLog( couponId, "📍[API]COUPON_GIVE_ID", "쿠폰증정API");

        couponUserService.giveCouponByUserId(authUser, couponId, couponGiveRequest);
    }

    @PostMapping("/expire")
    public ResponseEntity<Void> expireCoupons() {
        couponService.expireCoupons();
        return ResponseEntity.ok().build();
    }

}
