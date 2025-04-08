package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponUserService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final UserRepository userRepository;

    //admin- 해당유저에게 쿠폰주기
    @Transactional
    public void giveCouponByUserId(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest){
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        };

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 쿠폰이 없습니다."));

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new IllegalArgumentException(" 찾는 유저가 없습니다. "));//user는 쿠폰 리스트를 갖고 있음.

        if(couponGiveRequest.getAmount()>coupon.getAmount())
            throw new IllegalArgumentException("남은 쿠폰이 부족합니다");

     //   coupon.setUsers(users);

//user, coupon객체에 couponuser 객체 생성 후 저장.
        for (int i = 0; i < couponGiveRequest.getAmount(); i++) {
            CouponUser couponUser = new CouponUser();
            couponUser.setUsers(users);
            couponUser.setCoupons(coupon);
            couponUserRepository.save(couponUser);
        }

        coupon.discountCoupon(couponGiveRequest.getAmount());

    }
}
