package com.example.auctionmarket.domain.payment.service;

//import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.entity.Auction;
import com.example.auctionmarket.domain.auction.repository.AuctionRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public void createPayment(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                ()-> new PaymentException(PaymentErrorCode.NOT_FOUND_AUCTION)
        );

        Payment payment = Payment.builder()
                .user_id(auction.getConsumerId())
                .auction_id(auction.getId())
                .pay_status(PayStatus.PENDING)
                .amount(auction.getMaxPrice())
                .deadline(LocalDateTime.now().plusDays(1))
                .build();
        paymentRepository.save(payment);
    }

    @Transactional
    public void confirmPayment (Long paymentId, PaymentRequest paymentRequest) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getUser_id().equals(paymentRequest.getUserId())) {
            throw new PaymentException(PaymentErrorCode.NOT_PAYMENT_OWNER);
        }

        if (!payment.getAmount().equals(paymentRequest.getAmount())) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        // 후순위자에게 기회가 넘어간 경우
        // 환불이 된 상태
        if (payment.getPay_status().equals(PayStatus.REFUNDED)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
        }

        PayType payType = PayType.of(paymentRequest.getPay_type());

        payment.completePayment(payType);
    }

    @Transactional
    public void refundPayment (Long paymentId, RefundRequest refundRequest) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다"));

        PayType refundType = PayType.of(refundRequest.getPay_type());
        if (payment.getPay_type() != refundType) {
            throw new PaymentException(PaymentErrorCode.INVALID_PAY_TYPE);
        }
        // 환불가능 여부 확인
        payment.canRefund();

        // 환불 처리
        payment.refund();

        Refund refund = Refund.builder()
                .payment(payment)
                .payType(refundType)
                .description(refundRequest.getDescription())
                .refundedAt(LocalDateTime.now())
                .build();

        refundRepository.save(refund);
    }

}
