package com.example.auctionmarket.domain.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = 168126541L;

    public static final QPayment payment = new QPayment("payment");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> auctionId = createNumber("auctionId", Long.class);

    public final BooleanPath couponUsed = createBoolean("couponUsed");

    public final NumberPath<Long> couponUserId = createNumber("couponUserId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> deadline = createDateTime("deadline", java.time.LocalDateTime.class);

    public final NumberPath<Long> discountAmount = createNumber("discountAmount", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> payDate = createDateTime("payDate", java.time.LocalDateTime.class);

    public final EnumPath<com.example.auctionmarket.domain.payment.enums.PayStatus> payStatus = createEnum("payStatus", com.example.auctionmarket.domain.payment.enums.PayStatus.class);

    public final EnumPath<com.example.auctionmarket.domain.payment.enums.PayType> payType = createEnum("payType", com.example.auctionmarket.domain.payment.enums.PayType.class);

    public final DateTimePath<java.time.LocalDateTime> refundDeadline = createDateTime("refundDeadline", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}

