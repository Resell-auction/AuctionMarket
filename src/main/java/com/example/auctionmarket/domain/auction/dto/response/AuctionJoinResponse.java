package com.example.auctionmarket.domain.auction.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class AuctionJoinResponse {
    private final Long auctionId;
    private final String productName;
    private final Long minPrice;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String websocketUrl;

    public AuctionJoinResponse(Long auctionId, String productName, Long minPrice, LocalDateTime startTime, LocalDateTime endTime, String websocketUrl) {
        this.auctionId = auctionId;
        this.productName = productName;
        this.minPrice = minPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.websocketUrl = websocketUrl;
    }
}
