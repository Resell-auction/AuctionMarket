package com.example.auctionmarket.domain.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = 1439025139L;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final com.example.auctionmarket.common.entity.QTimeStamped _super = new com.example.auctionmarket.common.entity.QTimeStamped(this);

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final StringPath couponName = createString("couponName");

    public final EnumPath<com.example.auctionmarket.domain.coupon.enums.CouponStatus> couponStatus = createEnum("couponStatus", com.example.auctionmarket.domain.coupon.enums.CouponStatus.class);

    public final EnumPath<com.example.auctionmarket.domain.coupon.enums.CouponType> couponType = createEnum("couponType", com.example.auctionmarket.domain.coupon.enums.CouponType.class);

    public final ListPath<CouponUser, QCouponUser> couponUserList = this.<CouponUser, QCouponUser>createList("couponUserList", CouponUser.class, QCouponUser.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> discountAmount = createNumber("discountAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> expiredAt = createDateTime("expiredAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QCoupon(String variable) {
        super(Coupon.class, forVariable(variable));
    }

    public QCoupon(Path<? extends Coupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoupon(PathMetadata metadata) {
        super(Coupon.class, metadata);
    }

}

