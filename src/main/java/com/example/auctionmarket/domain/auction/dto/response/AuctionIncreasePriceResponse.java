package com.example.auctionmarket.domain.auction.dto.response;

import com.example.auctionmarket.domain.product.enums.ProductCategory;
import lombok.Getter;

@Getter
public class AuctionIncreasePriceResponse {
    private final Long id;
    private final Long productId;
    private final Long consumerId;
    private final String productName;
    private final ProductCategory category;
    private final Long minPrice;
    private final Long maxPrice;

    public AuctionIncreasePriceResponse(Long id, Long productId, Long consumerId, String productName, ProductCategory category, Long minPrice, Long maxPrice) {
        this.id = id;
        this.productId = productId;
        this.consumerId = consumerId;
        this.productName = productName;
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
