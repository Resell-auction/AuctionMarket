package com.example.auctionmarket.domain.auction.dto.response;

import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuctionSaveResponse {
    private final Long id;
    private final Long productId;
    private final Long userId;
    private final String productName;
    private final ProductCategory category;
    private final Long minPrice;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final AuctionStatus status;
    private final String websocketUrl;

    public AuctionSaveResponse(Long id, Long productId, Long userId, String productName, ProductCategory category, Long minPrice, LocalDateTime startTime, LocalDateTime endTime, AuctionStatus status, String websocketUrl) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.productName = productName;
        this.category = category;
        this.minPrice = minPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.websocketUrl = websocketUrl;
    }
}
