package com.example.auctionmarket.domain.coupon.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.coupon.dto.CouponRequest;
import com.example.auctionmarket.domain.coupon.dto.CouponResponse;
import com.example.auctionmarket.domain.coupon.dto.CouponUpdateRequest;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.coupon.exception.CouponException;
import com.example.auctionmarket.domain.coupon.repository.CouponRepository;
import com.example.auctionmarket.domain.user.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private Authentication authUser;

    @Test
    void 쿠폰_생성_성공(){
        //given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");
        CouponRequest couponRequest= new CouponRequest("coupon1","description1",10L,  expiredAt, 10, CouponType.PERCENT);
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,10,CouponType.PERCENT);
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ADMIN")
        );
        given(couponRepository.save(Mockito.<Coupon>any())).willReturn(coupon);

        //when
        CouponResponse couponResponse = couponService.createCoupon(authUser, couponRequest);

        //then
        assertThat(couponResponse).isNotNull();
    }

    @Test
    void admin이_아닌_유저가_생성을_시도하면_에러가_발생한다(){
        //given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.USER,"nickname");
        CouponRequest couponRequest= new CouponRequest("coupon1","description1",10L,  expiredAt, 10, CouponType.PERCENT);

        //when&then
        assertThrows(CouponException.class, ()->couponService.createCoupon(authUser,couponRequest));
    }


    @Test
    void 쿠폰_목록_조회_성공(){
        // given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        List<Coupon> mockCoupons = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> new Coupon("coupon1","description1",10L,expiredAt,10,CouponType.PERCENT))
                .collect(Collectors.toList());

        given(couponRepository.findByCouponStatus(CouponStatus.VALID)).willReturn(mockCoupons);

        // when
        List<CouponResponse> result = couponService.findAll();

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.get(0).getCouponName()).isEqualTo("coupon1");

        verify(couponRepository).findByCouponStatus(CouponStatus.VALID);
    }

    @Test
    void 쿠폰_단건_조회_성공(){
        // given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,10,CouponType.PERCENT);

        given(couponRepository.findById(coupon.getId())).willReturn(Optional.of(coupon));

        // when
        CouponResponse couponResponse = couponService.findById(coupon.getId());

        // then
        assertThat(couponResponse).isNotNull();
        assertThat(coupon.getAmount()).isEqualTo(10);
    }

    @Test
    void 쿠폰_단건_조회_시_찾는_쿠폰이_없으면_에러가_발생한다(){
        //given
        given(couponRepository.findById(1L)).willReturn(Optional.empty());

        //when&then
        assertThrows(CouponException.class, ()->couponService.findById(1L));
    }

    @Test
    void 쿠폰_수정_성공(){
        // given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,10,CouponType.PERCENT);
        CouponUpdateRequest couponUpdateRequest= new CouponUpdateRequest("coupon2","description2", 10L, expiredAt);
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");

        given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));

        coupon.update(couponUpdateRequest.getCouponName(),
                couponUpdateRequest.getDescription(),
                couponUpdateRequest.getDiscountAmount(),
                expiredAt);

        // when
        CouponResponse couponResponse = couponService.updateById(authUser, 1L, couponUpdateRequest);

        // then
        assertThat(couponResponse).isNotNull();
        assertThat(coupon.getCouponName()).isEqualTo("coupon2");
        assertThat(coupon.getDescription()).isEqualTo("description2");
    }

    @Test
    void 수정시_잘못된_쿠폰_아이디를_입력하면_에러가_발생한다(){
        // given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        CouponUpdateRequest couponUpdateRequest= new CouponUpdateRequest("coupon2","description2", 10L, expiredAt);
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");

        given(couponRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThrows(CouponException.class, ()->couponService.updateById(authUser,1L, couponUpdateRequest));
    }

    @Test
    void admin이_아닌_유저가_수정을_시도하면_에러가_발생한다(){
        // given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        CouponUpdateRequest couponUpdateRequest= new CouponUpdateRequest("coupon2","description2", 10L, expiredAt);
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.USER,"nickname");

        // when & then
        assertThrows(CouponException.class, ()->couponService.updateById(authUser,1L,couponUpdateRequest));
    }

    @Test
    void 쿠폰_삭제_성공(){
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");
        Coupon coupon = new Coupon("coupon1","description1",10L,expiredAt,10,CouponType.PERCENT);
        given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));

        couponService.deleteById(authUser,1L);

        assertThat(coupon.getCouponStatus()).isEqualTo(CouponStatus.EXPIRED);
    }

    @Test
    void 삭제시_없는_쿠폰_아이디를_입력하면_에러가_발생한다(){
        //given
        LocalDateTime expiredAt = LocalDateTime.parse("2025-05-05T00:00:00");
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.ADMIN,"nickname");

        given(couponRepository.findById(1L)).willReturn(Optional.empty());

        //when&then
        assertThrows(CouponException.class, ()->couponService.deleteById(authUser,1L));
    }

    @Test
    void admin이_아닌_유저가_삭제를_시도하면_에러가_발생한다(){
        //given
        AuthUser authUser = new AuthUser(1L,"abc@naver.com", Role.USER,"nickname");

        //when&then
        assertThrows(CouponException.class, ()->couponService.deleteById(authUser,1L));
    }


}
