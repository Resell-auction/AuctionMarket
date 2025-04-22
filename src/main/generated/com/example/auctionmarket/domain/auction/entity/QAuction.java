package com.example.auctionmarket.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuction is a Querydsl query type for Auction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuction extends EntityPathBase<Auction> {

    private static final long serialVersionUID = -52836691L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuction auction = new QAuction("auction");

    public final com.example.auctionmarket.common.entity.QTimeStamped _super = new com.example.auctionmarket.common.entity.QTimeStamped(this);

    public final NumberPath<Long> consumerId = createNumber("consumerId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ComparablePath<java.time.Duration> duration = createComparable("duration", java.time.Duration.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> maxPrice = createNumber("maxPrice", Long.class);

    public final NumberPath<Long> minPrice = createNumber("minPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.example.auctionmarket.domain.product.entity.QProduct product;

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final EnumPath<com.example.auctionmarket.domain.auction.enums.AuctionStatus> status = createEnum("status", com.example.auctionmarket.domain.auction.enums.AuctionStatus.class);

    public QAuction(String variable) {
        this(Auction.class, forVariable(variable), INITS);
    }

    public QAuction(Path<? extends Auction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuction(PathMetadata metadata, PathInits inits) {
        this(Auction.class, metadata, inits);
    }

    public QAuction(Class<? extends Auction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.example.auctionmarket.domain.product.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

