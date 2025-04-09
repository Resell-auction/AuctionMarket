package com.example.auctionmarket.domain.payment.entity;

import com.example.auctionmarket.domain.payment.enums.PayStatus;
import com.example.auctionmarket.domain.payment.enums.PayType;
import com.example.auctionmarket.domain.payment.exception.PaymentErrorCode;
import com.example.auctionmarket.domain.payment.exception.PaymentException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 결제 아이디

//    private Long orderId; // 결제 요청 아이디
    private Long userId; // 유저 아이디
    private Long auctionId; // 경매 아이디

    @Enumerated(EnumType.STRING)
    private PayType payType; // 결제 수단
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus; // 결제 상태

    private Long amount; // 총 금액
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
        if (this.payStatus == PayStatus.COMPLETED) {
            throw new PaymentException(PaymentErrorCode.ALREADY_COMPLETED_PAYMENT);
        }
        if (this.deadline.isBefore(LocalDateTime.now())) {
            throw new PaymentException(PaymentErrorCode.DEADLINE_EXPIRED_PAYMENT);
        }
        this.payType = payType;
        this.payStatus = PayStatus.COMPLETED;
        this.payDate = LocalDateTime.now();
        this.refundDeadline = LocalDateTime.now().plusDays(1);
    }

    // 환불 가능 여부 체크
    public void canRefund() {
        if (this.payStatus != PayStatus.COMPLETED) {
            throw new PaymentException(PaymentErrorCode.NOT_COMPLETED_PAYMENT);
        }
        if (this.payStatus.equals(PayStatus.REFUNDED)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
        }
        if (this.refundDeadline.isBefore(LocalDateTime.now())) {
            throw new PaymentException(PaymentErrorCode.DEADLINE_EXPIRED_PAYMENT);
        }
    }

    public void refund() {
        this.payStatus = PayStatus.REFUNDED;
    }
}
