package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
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

    @Transactional
    public CouponResponse createCoupon(CouponRequest couponRequest) {
        //나중에 AuthUser 추가 admin 권한 검사.
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

    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {
        List<Coupon> coupons = couponRepository.findAll();
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

    @Transactional(readOnly = true)
    public CouponResponse findById(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        return new CouponResponse(coupon.getId(),
                coupon.getCouponName(),
                coupon.getDescription(),
                coupon.getDiscountRate(),
                coupon.getExpiredAt(),
                coupon.getAmount());
    }

    @Transactional
    public CouponResponse updateById(Long id, CouponUpdateRequest couponUpdateRequest) {
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

    @Transactional
    public void deleteById(Long id) {//enum으로 수정
        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        couponRepository.deleteById(id);
    }
}
