package com.example.auctionmarket.domain.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -2027874003L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final com.example.auctionmarket.common.entity.QTimeStamped _super = new com.example.auctionmarket.common.entity.QTimeStamped(this);

    public final NumberPath<Long> auctionId = createNumber("auctionId", Long.class);

    public final EnumPath<com.example.auctionmarket.domain.product.enums.ProductCategory> category = createEnum("category", com.example.auctionmarket.domain.product.enums.ProductCategory.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath productContent = createString("productContent");

    public final StringPath productName = createString("productName");

    public final EnumPath<com.example.auctionmarket.domain.product.enums.SoldStatus> soldStatus = createEnum("soldStatus", com.example.auctionmarket.domain.product.enums.SoldStatus.class);

    public final com.example.auctionmarket.domain.user.entity.QUser user;

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.auctionmarket.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

