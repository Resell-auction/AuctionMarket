package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.aop.DistributedLock;
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
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUserService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    //기본.락이 없을 때(coupon-@Version삭제)
    @Transactional
    public void giveCouponByUserId(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest) {

        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        Coupon coupon = couponRepository.findById(id).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

        User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));//user는 쿠폰 리스트를 갖고 있음.

        if (couponGiveRequest.getAmount() > 1)
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);

        //user, coupon객체에 couponuser 객체 생성 후 저장.
        CouponUser couponUser = new CouponUser();
        couponUser.setUsers(users);
        couponUser.setCoupons(coupon);
        couponUserRepository.save(couponUser);

        coupon.assignUniqueCoupon();
    }

    //낙관적락
    @Transactional
    public void giveCouponByUserId2(AuthUser authUser, Long id, CouponGiveRequest couponGiveRequest) {

        if (!authUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new CouponException(CouponErrorCode.NOT_ADMIN_AUTHORITY);
        }

        int retry = 3; // 최대 3번까지 재시도
        while (retry-- > 0) {
            try {
                Coupon coupon = couponRepository.findById(id).orElseThrow(
                        () -> new CouponException(CouponErrorCode.NOT_FOUND_COUPON));

                User users = userRepository.findById(couponGiveRequest.getUserId()).orElseThrow(
                        () -> new CouponException(CouponErrorCode.NOT_FOUND_USER));

                if (couponGiveRequest.getAmount() > 1)
                    throw new CouponException(CouponErrorCode.DUPLICATE_COUPON);

                CouponUser couponUser = new CouponUser();
                couponUser.setUsers(users);
                couponUser.setCoupons(coupon);
                couponUserRepository.save(couponUser);

                coupon.assignUniqueCoupon(); // 여기서 version 업데이트 (낙관적 락 충돌 위험)

                return; // 성공했으면 바로 리턴

            } catch (ObjectOptimisticLockingFailureException e) {
                if (retry == 0) {
                    throw new CouponException(CouponErrorCode.ISSUED_COUPON); // 재시도 끝나면 실패 처리
                }
            }
        }
    }

    //비관적락(version지울것)
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
    }

    //분산락+낙관적락(분산락만 테스트하려면 version을 지울것)
    @DistributedLock(key = "'coupon:' + #couponId")
    @Transactional
    @Retryable(
            value = {
                    ObjectOptimisticLockingFailureException.class,
                    CannotAcquireLockException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(delay = 200)
    )
    public void giveCouponByUserId5(AuthUser authUser, Long couponId, CouponGiveRequest couponGiveRequest) {
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

        coupon.assignUniqueCoupon(); // 이게 version 충돌 원인
    }
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
