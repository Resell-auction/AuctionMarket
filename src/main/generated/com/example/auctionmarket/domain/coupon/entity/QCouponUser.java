package com.example.auctionmarket.domain.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouponUser is a Querydsl query type for CouponUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponUser extends EntityPathBase<CouponUser> {

    private static final long serialVersionUID = -317524386L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouponUser couponUser = new QCouponUser("couponUser");

    public final QCoupon coupons;

    public final EnumPath<com.example.auctionmarket.domain.coupon.enums.CouponType> couponType = createEnum("couponType", com.example.auctionmarket.domain.coupon.enums.CouponType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath used = createBoolean("used");

    public final com.example.auctionmarket.domain.user.entity.QUser users;

    public QCouponUser(String variable) {
        this(CouponUser.class, forVariable(variable), INITS);
    }

    public QCouponUser(Path<? extends CouponUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouponUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouponUser(PathMetadata metadata, PathInits inits) {
        this(CouponUser.class, metadata, inits);
    }

    public QCouponUser(Class<? extends CouponUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupons = inits.isInitialized("coupons") ? new QCoupon(forProperty("coupons")) : null;
        this.users = inits.isInitialized("users") ? new com.example.auctionmarket.domain.user.entity.QUser(forProperty("users")) : null;
    }

}

