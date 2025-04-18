package com.example.auctionmarket.domain.auction.dto.response;

import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuctionResponse {

    private final Long id;
    private final Long productId;
    private final Long userId;
    private final String productName;
    private final ProductCategory category;
    private final Long minPrice;
    private final Long maxPrice;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final AuctionStatus status;
    private final String remainingTime;
    private final String websocketUrl;

    public AuctionResponse(Long id, Long productId, Long userId, String productName, ProductCategory category, Long minPrice, Long maxPrice,
                           LocalDateTime startTime, LocalDateTime endTime, AuctionStatus status, String remainingTime, String websocketUrl) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.productName = productName;
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.remainingTime = remainingTime;
        this.websocketUrl = websocketUrl;
    }
}
