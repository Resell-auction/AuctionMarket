package com.example.auctionmarket.domain.auction.dto.response;

import lombok.Getter;

@Getter
public class AuctionResponse {

    private final Long id;
    private final Long productId;
    private final Long productName;
    private final String category;
    private final Long minPrice;
    private final Long progressTime;

    public AuctionResponse(Long id, Long productId, Long productName, String category, Long minPrice, Long progressTime) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.minPrice = minPrice;
        this.progressTime = progressTime;
    }
}
