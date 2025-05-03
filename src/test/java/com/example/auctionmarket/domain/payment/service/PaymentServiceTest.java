package com.example.auctionmarket.domain.payment.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.payment.dto.request.PaymentRequest;
import com.example.auctionmarket.domain.payment.entity.Payment;
import com.example.auctionmarket.domain.payment.enums.PayStatus;
import com.example.auctionmarket.domain.payment.enums.PayType;
import com.example.auctionmarket.domain.payment.repository.PaymentRepository;
import com.example.auctionmarket.domain.payment.repository.RefundRepository;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.SoldStatus;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private RefundRepository refundRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CouponUserRepository couponUserRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제를_생성할_수_있다() {
        // given
        Long auctionId = 1L;
        Long consumerId = 2L;
        Long maxPrice = 10000L;

        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setConsumerId(consumerId);
        auction.setMaxPrice(maxPrice);

        given(auctionRepository.findById(auctionId)).willReturn(Optional.of(auction));
        // when
        LocalDateTime before = LocalDateTime.now();
        paymentService.createPayment(auctionId, consumerId, maxPrice);
        LocalDateTime after = LocalDateTime.now();
        // then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentArgumentCaptor.capture());

        Payment payment = paymentArgumentCaptor.getValue();

        assertThat(payment.getUserId()).isEqualTo(consumerId);
        assertThat(payment.getAuctionId()).isEqualTo(auctionId);
        assertThat(payment.getAmount()).isEqualTo(maxPrice);
        assertThat(payment.getPayStatus()).isEqualTo(PayStatus.PENDING);

        assertThat(payment.getDeadline()).isAfterOrEqualTo(before.plusDays(1));
        assertThat(payment.getDeadline()).isBeforeOrEqualTo(after.plusDays(1));
    }

    @Test
    void 결제를_진행한다() {
        // given
        Long auctionId = 1L;
        Long consumerId = 2L;
        Long paymentId = 1L;
        Long amount = 10000L;
        Long productId = 1L;
        Long couponId = 1L;

        // 제품 객체 생성
        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", productId);

        // 경매 객체 생성
        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setConsumerId(consumerId);
        auction.setMaxPrice(amount);
        ReflectionTestUtils.setField(auction, "product", product);

        // 결제 객체 생성
        Payment payment = Payment.builder()
                .userId(2L)
                .auctionId(1L)
                .payStatus(PayStatus.PENDING)
                .amount(10000L)
                .deadline(LocalDateTime.now().plusMinutes(10))
                .build();
        // 유저 객체 생성
        AuthUser authUser = new AuthUser(2L, "1234", Role.USER, "tester");
        // 결제 요청 생성
        PaymentRequest request = new PaymentRequest("POINT", amount, couponId);
        ReflectionTestUtils.setField(request, "amount", amount);
        ReflectionTestUtils.setField(request, "payType", "POINT");
        ReflectionTestUtils.setField(request, "couponId", couponId);

        // 쿠폰 객체 생성
        Coupon coupon = new Coupon(
                "할인 쿠폰",
                "20%할인쿠폰" ,
                20L,
                LocalDateTime.parse("2025-05-05T00:00:00"),
                1,
                CouponType.PERCENT);
        ReflectionTestUtils.setField(coupon, "id", couponId);
        // 쿠폰 유저 객체 생성
        CouponUser couponUser = new CouponUser();
        ReflectionTestUtils.setField(couponUser, "coupons", coupon);
        ReflectionTestUtils.setField(couponUser, "couponType", coupon.getCouponType());

        User user = new User();
        ReflectionTestUtils.setField(user, "id", authUser.getId());
        ReflectionTestUtils.setField(couponUser, "users", user);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(auctionRepository.findById(auctionId)).willReturn(Optional.of(auction));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(couponUserRepository.findById(couponId)).willReturn(Optional.of(couponUser));

        // when
        paymentService.confirmPayment(paymentId, request, authUser);
        // then
        assertThat(payment.getPayStatus()).isEqualTo(PayStatus.COMPLETED);
        assertThat(product.getSoldStatus()).isEqualTo(SoldStatus.SOLD);
        assertThat(couponUser.isUsed()).isTrue();
        assertThat(payment.getCouponUserId()).isEqualTo(couponUser.getId());
        assertThat(payment.getPayType()).isEqualTo(PayType.POINT);
    }

    @Test
    void 환불을_정상적으로_진행한다() {

    }
}