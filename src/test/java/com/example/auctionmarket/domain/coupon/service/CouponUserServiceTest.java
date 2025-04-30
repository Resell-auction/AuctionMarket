package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponGiveRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CouponUserServiceTest {

    @InjectMocks
    private CouponUserService couponUserService;

    @Mock
    private CouponUserRepository couponUserRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authUser;

    @Test
    void 쿠폰_증정_성공(){
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        CouponGiveRequest couponGiveRequest= new CouponGiveRequest(1L,4);
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,10, CouponType.PERCENT);
        User user = Mockito.mock(User.class);
    //    CouponUser couponUser = new CouponUser(1L, user, coupon, CouponType.PERCENT, false);

        given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        couponUserService.giveCouponByUserId(authUser, 1L, couponGiveRequest);

        assertThat(coupon.getAmount()).isEqualTo(6);
    //    assertThat(couponUser).isNotNull();
    }

    @Test
    void admin이_유저가_증정을_시도하면_에러가_발생한다(){
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.USER,"nickname");
        CouponGiveRequest couponGiveRequest= new CouponGiveRequest(1L,4);

        assertThrows(CouponException.class, () -> couponUserService.giveCouponByUserId( authUser,1L, couponGiveRequest));
    }

    @Test
    void 쿠폰증정시_수량이상을_입력하면_에러가_발생한다(){
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        CouponGiveRequest couponGiveRequest= new CouponGiveRequest(1L,6);
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,4, CouponType.PERCENT);
        User user = Mockito.mock(User.class);

        given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        assertThrows(CouponException.class, () -> couponUserService.giveCouponByUserId(authUser,1L, couponGiveRequest));
    }

    @Test
    void 쿠폰증정시_없는_유저아이디를_입력하면_에러가_발생한다(){
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");
        CouponGiveRequest couponGiveRequest= new CouponGiveRequest(1L,6);
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,4, CouponType.PERCENT);

        given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(CouponException.class, () -> couponUserService.giveCouponByUserId(authUser,1L, couponGiveRequest));
    }

    @Test
    void 쿠폰증정시_없는_쿠폰아이디를_입력하면_에러가_발생한다(){
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");
        CouponGiveRequest couponGiveRequest= new CouponGiveRequest(1L,6);
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,4, CouponType.PERCENT);

        given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));
        given(couponRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(CouponException.class, () -> couponUserService.giveCouponByUserId(authUser,1L, couponGiveRequest));
    }
}
