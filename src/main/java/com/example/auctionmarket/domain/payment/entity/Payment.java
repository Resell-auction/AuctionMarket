package com.example.auctionmarket.domain.payment.entity;

import com.example.auctionmarket.domain.payment.enums.PayStatus;
import com.example.auctionmarket.domain.payment.enums.PayType;
import com.example.auctionmarket.domain.payment.exception.PaymentErrorCode;
import com.example.auctionmarket.domain.payment.exception.PaymentException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 결제 아이디

    private Long userId; // 유저 아이디
    private Long auctionId; // 경매 아이디
    private Long couponUserId;

    @Enumerated(EnumType.STRING)
    private PayType payType; // 결제 수단
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus; // 결제 상태

    private Long amount; // 총 금액
    private boolean couponUsed; // 쿠폰 사용여부
    private Long discountAmount; // 할인율
    private LocalDateTime deadline; // 결제 기한
    private LocalDateTime payDate; // 결제완료 시간
    private LocalDateTime refundDeadline; // 환불일(결제후 24시간동안에만 환불 가능)

    @Builder
    public Payment(Long userId, Long auctionId, PayStatus payStatus, Long amount, LocalDateTime deadline) {
        this.userId = userId;
        this.auctionId = auctionId;
        this.payStatus = payStatus;
        this.amount = amount;
        this.deadline = deadline;
    }

    // 결제 상태 변경
    public void completePayment(PayType payType) {
        // 결제가 완료된 상태면 결제불가
        if (this.payStatus == PayStatus.COMPLETED) {
            throw new PaymentException(PaymentErrorCode.ALREADY_COMPLETED_PAYMENT);
        }
        // 결제 데드라인을 넘었으면 결제 불가
        if (this.deadline.isBefore(LocalDateTime.now())) {
            throw new PaymentException(PaymentErrorCode.DEADLINE_EXPIRED_PAYMENT);
        }
        this.payType = payType;
        this.payStatus = PayStatus.COMPLETED;
        this.payDate = LocalDateTime.now();
        this.refundDeadline = this.payDate.plusDays(1);
    }

    // 환불 가능 여부 체크
    public void canRefund() {
        if (this.payStatus.equals(PayStatus.REFUNDED)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
        }
        if (this.payStatus != PayStatus.COMPLETED) {
            throw new PaymentException(PaymentErrorCode.NOT_COMPLETED_PAYMENT);
        }
        if (this.refundDeadline.isBefore(LocalDateTime.now())) {
            throw new PaymentException(PaymentErrorCode.DEADLINE_EXPIRED_PAYMENT);
        }
    }

    public void refund() {
        this.payStatus = PayStatus.REFUNDED;
    }

    public void applyCoupon(Long discountAmount, Long couponUserId) {
        this.amount -= discountAmount;
        this.discountAmount = discountAmount;
        this.couponUserId = couponUserId;
        this.couponUsed = true;
    }
}