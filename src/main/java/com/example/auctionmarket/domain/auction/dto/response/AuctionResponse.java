package com.example.auctionmarket.domain.auction.dto.response;

import java.time.LocalDateTime;

import com.example.auctionmarket.domain.auction.enums.AuctionStatus;
import com.example.auctionmarket.domain.product.enums.ProductCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuctionResponse {

    private Long id;
    private Long productId;
    private Long userId;
    private String productName;
    private ProductCategory category;
    private Long minPrice;
    private Long maxPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private String remainingTime;
    private String websocketUrl;

    public AuctionResponse(Long id, Long productId, Long userId, String productName, ProductCategory category, Long minPrice, Long maxPrice,
                           LocalDateTime startTime, LocalDateTime endTime, AuctionStatus status, String remainingTime) {
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
    }
}
