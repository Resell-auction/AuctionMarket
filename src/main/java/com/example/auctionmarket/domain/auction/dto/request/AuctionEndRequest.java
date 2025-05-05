package com.example.auctionmarket.domain.auction.dto.request;

import lombok.Getter;

@Getter
public class AuctionEndRequest {
    private Long auctionId;
    private Long consumerId;
    private Long amount;
}
