
package com.example.auctionmarket.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class CouponLockServiceTest {

    @InjectMocks
    private CouponUserService couponUserService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponUserRepository couponUserRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void 정상_쿠폰_발급() throws InterruptedException {

        // given
        AuthUser adminUser = new AuthUser(1L, "admin@email.com", Role.ADMIN, "관리자");

        Coupon coupon = new Coupon("이름", "설명", 10L, LocalDateTime.now().plusDays(1), 100, CouponType.PERCENT);

        User user = new User("email@email.com","password","nickname","phoneNum",Role.USER);

        CouponGiveRequest request = new CouponGiveRequest(adminUser.getId(), 1);

        // stub
        when(couponRepository.findById(100L)).thenReturn(Optional.of(coupon));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        couponUserService.giveCouponByUserId(adminUser, 100L, request);

        // then
        verify(couponUserRepository).save(any(CouponUser.class));
    }

    @Test
    void 권한_없는_유저_예외() throws Exception {

        AuthUser normalUser = new AuthUser(1L, "abc@gmail.com", Role.USER, "nickname");

        CouponGiveRequest request = new CouponGiveRequest(1L, 1);

        assertThrows(CouponException.class, () ->
                couponUserService.giveCouponByUserId(normalUser, 100L, request));
    }

    @Test
    void 쿠폰_없음_예외() throws Exception {

        AuthUser adminUser = new AuthUser(1L, "admin@email.com", Role.ADMIN, "관리자");

        when(couponRepository.findById(100L)).thenReturn(Optional.empty());
        CouponGiveRequest request = new CouponGiveRequest(200L, 1);

        assertThrows(CouponException.class, () ->
                couponUserService.giveCouponByUserId(adminUser, 100L, request));
    }

    @Test
    void 유저_없음_예외() throws Exception {

        AuthUser adminUser = new AuthUser(1L, "admin@email.com", Role.ADMIN, "관리자");
        Coupon coupon = new Coupon("이름", "설명", 10L, LocalDateTime.now().plusDays(1), 100, CouponType.PERCENT);

        CouponGiveRequest request = new CouponGiveRequest(999L, 1);

        when(couponRepository.findById(100L)).thenReturn(Optional.of(coupon));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CouponException.class, () ->
                couponUserService.giveCouponByUserId(adminUser, 100L, request));
    }

    @Test
    void 중복_쿠폰_예외() throws Exception {

        CouponGiveRequest request = new CouponGiveRequest(2L, 2);
        // given
        AuthUser adminUser = new AuthUser(1L, "admin@email.com", Role.ADMIN, "관리자");

        Coupon coupon = new Coupon("이름", "설명", 10L, LocalDateTime.now().plusDays(1), 100, CouponType.PERCENT);

        User user = new User("email@email.com","password","nickname","phoneNum",Role.USER);

        when(couponRepository.findById(100L)).thenReturn(Optional.of(coupon));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(CouponException.class, () ->
                couponUserService.giveCouponByUserId(adminUser, 100L, request));
    }
}

