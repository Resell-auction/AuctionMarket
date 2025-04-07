package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    //admin - 쿠폰생성
    @Transactional
    public CouponResponse createCoupon(AuthUser authUser, CouponRequest couponRequest) {
        if(authUser.getUserRole()!=UserStatus.ADMIN){
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        };

        Coupon coupon = new Coupon(couponRequest.getCouponName(),
                couponRequest.getDescription(),
                couponRequest.getDiscountRate(),
                couponRequest.getExpiredAt(),
                couponRequest.getAmount());

        Coupon savedCoupon = couponRepository.save(coupon);

        return new CouponResponse(savedCoupon.getId(),
                savedCoupon.getCouponName(),
                savedCoupon.getDescription(),
                savedCoupon.getDiscountRate(),
                savedCoupon.getExpiredAt(),
                savedCoupon.getAmount());
    }

    //쿠폰목록조회
    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {
        List<Coupon> coupons = couponRepository.findByCouponStatus(CouponStatus.VALID);//사용가능한 쿠폰만 조회
        List<CouponResponse> couponList = new ArrayList<>();

        for (Coupon coupon : coupons)
            couponList.add(new CouponResponse(coupon.getId(),
                    coupon.getCouponName(),
                    coupon.getDescription(),
                    coupon.getDiscountRate(),
                    coupon.getExpiredAt(),
                    coupon.getAmount()));

        return couponList;
    }

    //쿠폰단건조회
    @Transactional(readOnly = true)
    public CouponResponse findById(AuthUser authUser, Long id) {
        if(authUser.getUserRole()!=UserStatus.ADMIN){
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        };

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        return new CouponResponse(coupon.getId(),
                coupon.getCouponName(),
                coupon.getDescription(),
                coupon.getDiscountRate(),
                coupon.getExpiredAt(),
                coupon.getAmount());
    }

    //admin- 쿠폰수정
    @Transactional
    public CouponResponse updateById(AuthUser authUser, Long id, CouponUpdateRequest couponUpdateRequest) {
        if(authUser.getUserRole()!=UserStatus.ADMIN){
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        };

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        coupon.update(couponUpdateRequest.getCouponName(),
                couponUpdateRequest.getDescription(),
                couponUpdateRequest.getDiscountRate(),
                couponUpdateRequest.getExpiredAt());

        return new CouponResponse(coupon.getId(),
                coupon.getCouponName(),
                coupon.getDescription(),
                coupon.getDiscountRate(),
                coupon.getExpiredAt(),
                coupon.getAmount());
    }

    //admin- 쿠폰삭제
    @Transactional
    public void deleteById(AuthUser authUser, Long id) {
        if(authUser.getUserRole()!=UserStatus.ADMIN){
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        };

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        coupon.expiredCoupon();
    }

    //admin- 해당유저에게 쿠폰주기
    @Transactional
    public void giveCouponByUserId(Long id, CouponGiveRequest couponGiveRequest){
        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        User user = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 유저가 없습니다. ");
        )//user는 쿠폰 리스트를 갖고 있음.

        user.setCoupon(couponGiveRequest.getAmount());

        coupon.discountCoupon(couponGiveRequest.getAmount());

    }
}
