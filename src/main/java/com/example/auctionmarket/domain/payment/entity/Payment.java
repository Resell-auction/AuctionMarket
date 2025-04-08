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
    private Long user_id; // 유저 아이디
    private Long auction_id; // 경매 아이디

    @Enumerated(EnumType.STRING)
    private PayType pay_type; // 결제 수단
    @Enumerated(EnumType.STRING)
    private PayStatus pay_status; // 결제 상태

    private Long amount; // 총 금액
    private Long discount_amount; // 할인율
    private LocalDateTime deadline; // 결제 기한
    private LocalDateTime pay_date; // 결제완료 시간
    private LocalDateTime refund_date; // 환불일(결제후 24시간동안에만 환불 가능)

    @Builder
    public Payment(Long user_id, Long auction_id, PayStatus pay_status, Long amount, LocalDateTime deadline) {
        this.user_id = user_id;
        this.auction_id = auction_id;
        this.pay_status = pay_status;
        this.amount = amount;
        this.deadline = deadline;
    }

    // 결제 상태 변경
    public void completePayment(PayType pay_type) {
        if (this.pay_status == PayStatus.COMPLETED) {
            throw new PaymentException(PaymentErrorCode.ALREADY_COMPLETED_PAYMENT);
        }
        if (this.deadline.isBefore(LocalDateTime.now())) {
            throw new PaymentException(PaymentErrorCode.DEADLINE_EXPIRED_PAYMENT);
        }
        this.pay_status = PayStatus.COMPLETED;
        this.pay_date = LocalDateTime.now();
    }

    // 환불 가능 여부 체크
    public void canRefund() {
        if (this.pay_status != PayStatus.COMPLETED) {
            throw new PaymentException(PaymentErrorCode.NOT_COMPLETED_PAYMENT);
        }
        if  (this.refund_date != null) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED);
        }
    }

    public void refund() {
        this.pay_status = PayStatus.REFUNDED;
        this.refund_date = LocalDateTime.now();
    }
}
