package com.example.auctionmarket.domain.auction.repository;

import com.example.auctionmarket.domain.auction.entity.Auction;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

public class AuctionRespositoryImpl implements AuctionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Auction> findBySearch(String keyword, String category, Pageable pageable) {
        QAuction auction = QAuction.auction;
        QProduct product = QProduct.product;

        //@OneToOne 관게이기에 innerJoin을 사용
        JPAQuery<Auction> query = queryFactory
                .selectFrom(auction)
                .join(auction.product, product)
                .distinct();

        //동적 검색 조건
        if(StringUtils.hasText(keyword)) {
            query.where(product.productName.containsIgnoreCase(keyword));
        }

        if(StringUtils.hasText(category)) {
            query.where(product.category.eq(category));
        }

        //페이징 적용
        List<Auction> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //총 개수 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(auction.countDistinct())
                .from(auction)
                .join(auction.product, product);

        if(StringUtils.hasText(keyword)) {
            query.where(product.productName.containsIgnoreCase(keyword));
        }

        if(StringUtils.hasText(category)) {
            query.where(product.category.eq(category));
        }

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
