package com.example.auctionmarket.domain.payment.entity;

import com.example.auctionmarket.domain.payment.enums.PayStatus;
import com.example.auctionmarket.domain.payment.enums.PayType;
import com.example.auctionmarket.domain.payment.exception.PaymentErrorCode;
import com.example.auctionmarket.domain.payment.exception.PaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

   private Payment payment;

   @BeforeEach
    void setUp() {
       payment = Payment.builder()
               .userId(1L)
               .auctionId(10L)
               .payStatus(PayStatus.PENDING)
               .amount(10000L)
               .deadline(LocalDateTime.now().plusMinutes(30))
               .build();
   }

    @Test
    void 결제를_성공적으로_진행한다() {
       // given
        LocalDateTime before = LocalDateTime.now();
        payment.completePayment(PayType.POINT);
        LocalDateTime after = LocalDateTime.now();

       // when
        assertThat(payment.getPayStatus()).isEqualTo(PayStatus.COMPLETED);
        assertThat(payment.getPayType()).isEqualTo(PayType.POINT);

        assertThat(payment.getPayDate()).isAfter(before);
        assertThat(payment.getPayDate()).isBefore(after);
       // then
        assertThat(payment.getRefundDeadline()).isEqualTo(payment.getPayDate().plusDays(1));
    }

    @Test
    void 결제상태가_완료라면_결제는_불가능() {
       // given
       payment.completePayment(PayType.POINT);
       // when
        assertThatThrownBy(()-> payment.completePayment(PayType.POINT))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining(PaymentErrorCode.ALREADY_COMPLETED_PAYMENT.getDefaultMessage());
       // then
    }

    @Test
    void 결제기한이_지나면_결제는_불가능() {
       // given
       Payment payment = Payment.builder()
               .userId(1L)
               .auctionId(100L)
               .payStatus(PayStatus.PENDING)
               .amount(10000L)
               .deadline(LocalDateTime.now().minusMinutes(2))
               .build();
       // when
        assertThatThrownBy(()-> payment.completePayment(PayType.POINT))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining(PaymentErrorCode.DEADLINE_EXPIRED_PAYMENT.getDefaultMessage());
       // then
    }

    @Test
    void 환불_가능_여부_체크_성공() {
       payment.completePayment(PayType.POINT);

       assertThatCode(()-> payment.canRefund()).doesNotThrowAnyException();
    }

    @Test
    void 결제를_안하면_환불을_할_수_없다() {
        Payment payment = Payment.builder()
                .userId(1L)
                .auctionId(100L)
                .payStatus(PayStatus.PENDING)
                .amount(10000L)
                .deadline(LocalDateTime.now().plusMinutes(10))
                .build();

        assertThatThrownBy(()-> payment.canRefund())
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining(PaymentErrorCode.NOT_COMPLETED_PAYMENT.getDefaultMessage());
    }

    @Test
    void 이미_환불한_결제는_환불할_수_없다() {
       payment.completePayment(PayType.POINT);
       payment.refund();

       assertThatThrownBy(()-> payment.canRefund())
               .isInstanceOf(PaymentException.class)
               .hasMessageContaining(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED.getDefaultMessage());
    }

    @Test
    void 환불기한_지나면_환불_불가() throws Exception{
        payment = Payment.builder()
                .userId(1L)
                .auctionId(10L)
                .payStatus(PayStatus.PENDING)
                .amount(10000L)
                .deadline(LocalDateTime.now().plusMinutes(30))
                .build();
        payment.completePayment(PayType.POINT);

        ReflectionTestUtils.setField(payment, "refundDeadline", LocalDateTime.now().minusDays(1));

        assertThatThrownBy(()-> payment.canRefund())
               .isInstanceOf(PaymentException.class)
               .hasMessageContaining(PaymentErrorCode.DEADLINE_EXPIRED_PAYMENT.getDefaultMessage());
    }
}