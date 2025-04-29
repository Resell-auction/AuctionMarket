package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.exception.CouponErrorCode;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
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

    //admin - 해당유저에게 쿠폰주기
    @Transactional
    public void giveCouponByUserId(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest){
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        };

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));//user는 쿠폰 리스트를 갖고 있음.

        if(couponGiveRequest.getAmount()>coupon.getAmount())
            throw new CouponException(CouponErrorCode.OUT_OF_COUPON);

        //user, coupon객체에 couponuser 객체 생성 후 저장.
        for (int i = 0; i < couponGiveRequest.getAmount(); i++) {
            CouponUser couponUser = new CouponUser();
            couponUser.setUsers(users);
            couponUser.setCoupons(coupon);
            couponUser.setCouponType(coupon.getCouponType());
            couponUserRepository.save(couponUser);
        }

        coupon.discountCoupon(couponGiveRequest.getAmount());

    }
}
