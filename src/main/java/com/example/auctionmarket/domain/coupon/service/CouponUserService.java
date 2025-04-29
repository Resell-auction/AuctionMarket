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
            couponUserRepository.save(couponUser);
        }

        coupon.assignUniqueCoupon();
        coupon.discountCoupon(couponGiveRequest.getAmount());

        couponRepository.save(coupon);

    }

    //낙관적락+분산락
    @DistributedLock(key = "'coupon:' + #id + ':user:' + #couponGiveRequest.userId")
    @Transactional
    public void giveCouponByUserId3(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest) {
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            logService.saveLog(403L, "❌ERROR", "권한이 없습니다.");
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> {
                    logService.saveLog(404L, "❌ERROR", "해당 쿠폰 조회를 실패했습니다.");
                    return new CouponException(CouponErrorCode.NOT_FOUND_COUPON);
                }
        );

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> {
                    logService.saveLog(404L, "❌ERROR", "해당 유저 조회를 실패했습니다.");

                    return new CouponException(CouponErrorCode.NOT_FOUND_USER);
                });//user는 쿠폰 리스트를 갖고 있음.

        if (couponGiveRequest.getAmount() > 1){
            logService.saveLog(404L, "❌ERROR", "쿠폰은 1장만 발급 가능합니다.");

            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);
        }

        //user, coupon객체에 couponuser 객체 생성 후 저장.
        CouponUser couponUser = new CouponUser();
        couponUser.setUsers(users);
        couponUser.setCoupons(coupon);
        couponUserRepository.save(couponUser);

        coupon.assignUniqueCoupon();

        String redisKey = "coupon_stock:" + id;
        Long remains = redisTemplate.opsForValue().decrement(redisKey);
        if (remains == null || remains < 0) {
            // *옵션* DB 롤백이나 예외 처리
            throw new IllegalStateException("Redis stock underflow");
        }

        logService.saveLog((long) coupon.getId(), "✅INFO", "해당 쿠폰 수정을 성공했습니다.");
     //   log.info("💾 쿠폰 저장 시도: coupon={}, version={}", coupon.getId(), coupon.getVersion());
    }

    //비관적락
    @Transactional
    public void giveCouponByUserId4(AuthUser authUser, Long couponId, CouponGiveRequest couponGiveRequest) {
        // 관리자 권한 확인
        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        // 비관적 락 걸고 쿠폰 조회
        Coupon coupon = couponRepository.findByIdWithPessimisticLock(couponId).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        // 사용자 조회
        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));

        // 지급 수량 검증
        if (couponGiveRequest.getAmount() > 1)
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);

        // CouponUser 생성 및 저장
        CouponUser couponUser = new CouponUser();
        couponUser.setUsers(users);
        couponUser.setCoupons(coupon);
        couponUserRepository.save(couponUser);

        // 수량 감소 및 유니크 쿠폰 설정
        coupon.assignUniqueCoupon();
        coupon.discountCoupon(couponGiveRequest.getAmount());
        couponRepository.save(coupon);
    }

    //분산락으로 쿠폰 한 장씩 발급
    @DistributedLock(key = "'coupon:' + #id + ':user:' + #couponGiveRequest.userId")
    @Transactional
    public void giveCouponByUserId5(AuthUser authUser,Long couponId, CouponGiveRequest couponGiveRequest){
        RLock lock = redissonClient.getLock("coupon_lock");

        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));

        if (couponGiveRequest.getAmount() > 1) {
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);
        }

        CouponUser couponUser = new CouponUser();
        couponUser.setUsers(users);
        couponUser.setCoupons(coupon);
        couponUserRepository.save(couponUser);

        coupon.assignUniqueCoupon();
        coupon.discountCoupon(couponGiveRequest.getAmount());
        couponRepository.save(coupon);


    }
//
//    @Transactional
//    public void issueCoupon(Long userId, Long couponId){
//
//        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
//                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));
//
//        User users = userRepository.findById(userId).orElseThrow(
//                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));//user는 쿠폰 리스트를 갖고 있음.
//
//        CouponUser couponUser = new CouponUser();
//        couponUser.setUsers(users);
//        couponUser.setCoupons(coupon);
//        couponUserRepository.save(couponUser);
//    }

}
