package com.example.auctionmarket.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1594553987L;

    public static final QUser user = new QUser("user");

    public final QTimeStamped _super = new QTimeStamped(this);

    public final ListPath<com.example.auctionmarket.domain.coupon.entity.CouponUser, com.example.auctionmarket.domain.coupon.entity.QCouponUser> couponUserList = this.<com.example.auctionmarket.domain.coupon.entity.CouponUser, com.example.auctionmarket.domain.coupon.entity.QCouponUser>createList("couponUserList", com.example.auctionmarket.domain.coupon.entity.CouponUser.class, com.example.auctionmarket.domain.coupon.entity.QCouponUser.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath refreshToken = createString("refreshToken");

    public final EnumPath<com.example.auctionmarket.domain.user.enums.Role> role = createEnum("role", com.example.auctionmarket.domain.user.enums.Role.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

