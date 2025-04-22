package com.example.auctionmarket.domain.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRefund is a Querydsl query type for Refund
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefund extends EntityPathBase<Refund> {

    private static final long serialVersionUID = 2144027729L;

    public static final QRefund refund = new QRefund("refund");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> paymentId = createNumber("paymentId", Long.class);

    public final EnumPath<com.example.auctionmarket.domain.payment.enums.PayType> payType = createEnum("payType", com.example.auctionmarket.domain.payment.enums.PayType.class);

    public final DateTimePath<java.time.LocalDateTime> refundedAt = createDateTime("refundedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRefund(String variable) {
        super(Refund.class, forVariable(variable));
    }

    public QRefund(Path<? extends Refund> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefund(PathMetadata metadata) {
        super(Refund.class, metadata);
    }

}

