package com.example.auctionmarket.domain.payment.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.coupon.repository.CouponUserRepository;
import com.example.auctionmarket.domain.payment.dto.request.PaymentRequest;
import com.example.auctionmarket.domain.payment.dto.request.RefundRequest;
import com.example.auctionmarket.domain.payment.entity.Payment;
import com.example.auctionmarket.domain.payment.entity.Refund;
import com.example.auctionmarket.domain.payment.enums.PayStatus;
import com.example.auctionmarket.domain.payment.enums.PayType;
import com.example.auctionmarket.domain.payment.exception.PaymentErrorCode;
import com.example.auctionmarket.domain.payment.exception.PaymentException;
import com.example.auctionmarket.domain.payment.repository.PaymentRepository;
import com.example.auctionmarket.domain.payment.repository.RefundRepository;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.SoldStatus;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;
    private final CouponUserRepository couponUserRepository;

    @Transactional
    public void createPayment(Long auctionId,Long consumerId, Long amount) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                ()-> new PaymentException(PaymentErrorCode.NOT_FOUND_AUCTION)
        );

        Payment payment = Payment.builder()
                .userId(consumerId)
                .auctionId(auction.getId())
                .payStatus(PayStatus.PENDING)
                .amount(amount)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        paymentRepository.save(payment);

    }

    @Transactional
    public void confirmPayment (Long paymentId, PaymentRequest paymentRequest, AuthUser authUser) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.NOT_FOUND_PAYMENT));

        // 결제 하려는 유저가 낙찰자가 맞는지 확인
        if (!Objects.equals(authUser.getId(), payment.getUserId())) {
            throw new PaymentException(PaymentErrorCode.NOT_PAYMENT_OWNER);
        }

        // 입력한 결제 금액이 실제 낙찰금액과 같은지 확인
        if (!payment.getAmount().equals(paymentRequest.getAmount())) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_AMOUNT);
        }
        // 후순위자에게 기회가 넘어간 경우
        // 환불이 된 상태인지 확인
        if (payment.getPayStatus().equals(PayStatus.REFUNDED)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
        }

        // 쿠폰 로직
        if (paymentRequest.getCouponId() != null) {
            CouponUser couponUser = couponUserRepository.findById(paymentRequest.getCouponId())
                    .orElseThrow(()-> new PaymentException(PaymentErrorCode.NOT_FOUND_COUPON));

            // 쿠폰을 가지고 있는 지 확인
            if (!Objects.equals(couponUser.getUsers().getId(), authUser.getId())) {
                throw new IllegalArgumentException("쿠폰 사용자가 아닙니다");
            }

            // 사용된 쿠폰인지 확인
            if (couponUser.isUsed()) {
                throw new IllegalArgumentException("이미 사용된 쿠폰입니다");
            }

            Long discountAmount = couponUser.getCoupons().calculateDiscountRate(payment.getAmount());
            payment.applyCoupon(discountAmount, couponUser.getId());

            couponUser.setUsed(true);
            couponUserRepository.save(couponUser);
        }

        PayType payType = PayType.of(paymentRequest.getPayType());

        payment.completePayment(payType);

        Auction auction = auctionRepository.findById(payment.getAuctionId())
                .orElseThrow(()-> new PaymentException(PaymentErrorCode.NOT_FOUND_AUCTION));

        Product product = productRepository.findById(auction.getProduct().getId())
                .orElseThrow(()-> new PaymentException(PaymentErrorCode.NOT_FOUND_PRODUCT));

        product.updateSoldStatus(SoldStatus.SOLD);
    }

    @Transactional
    public void refundPayment (Long paymentId, RefundRequest refundRequest, AuthUser authUser) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.NOT_FOUND_PAYMENT));

        if (!Objects.equals(authUser.getId(), payment.getUserId())) {
            throw new PaymentException(PaymentErrorCode.NOT_REFUND_OWNER);
        }

        PayType refundType = PayType.of(refundRequest.getPayType());
        // 결제수단 불일치 확인
        if (payment.getPayType() != refundType) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAY_TYPE);
        }
        // 환불가능 여부 확인
        payment.canRefund();

        // 환불 처리
        payment.refund();

        if (payment.isCouponUsed() && payment.getCouponUserId() != null) {
            CouponUser couponUser = couponUserRepository.findById(payment.getCouponUserId())
                    .orElseThrow(()-> new PaymentException(PaymentErrorCode.NOT_FOUND_COUPON));
            couponUser.setUsed(false);
        }

        Refund refund = Refund.builder()
                .paymentId(payment.getId())
                .payType(refundType)
                .description(refundRequest.getDescription())
                .refundedAt(LocalDateTime.now())
                .build();

        refund.completeRefund(refundType, refundRequest.getDescription());
        refundRepository.save(refund);

        Auction auction = auctionRepository.findById(payment.getAuctionId())
                .orElseThrow(()-> new PaymentException(PaymentErrorCode.NOT_FOUND_AUCTION));

        Product product = productRepository.findById(auction.getProduct().getId())
                .orElseThrow(()-> new PaymentException(PaymentErrorCode.NOT_FOUND_PRODUCT));

        product.updateSoldStatus(SoldStatus.UNSOLD);
    }

    public boolean shouldCreatePayment(Long auctionId) {
        return paymentRepository.findByAuctionId(auctionId).isEmpty();
    }
}
