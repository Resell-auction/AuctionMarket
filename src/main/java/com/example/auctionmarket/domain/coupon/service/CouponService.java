package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    //admin - 쿠폰생성
    @Transactional
    public CouponResponse createCoupon(CouponRequest couponRequest) {
//        if (authUser.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
//            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
//        };

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
    public CouponResponse findById( Long id) {

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
    public CouponResponse updateById( Long id, CouponUpdateRequest couponUpdateRequest) {
//        if (authUser.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
//            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
//        };

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
    public void deleteById( Long id) {
//        if (authUser.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
//            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
//        };

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        coupon.expiredCoupon();
    }


}
