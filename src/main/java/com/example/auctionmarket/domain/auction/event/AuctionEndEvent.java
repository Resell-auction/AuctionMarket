package com.example.auctionmarket.domain.auction.event;

import lombok.Getter;

@Getter
public class AuctionEndEvent {
    private final Long auctionId;
    private final Long cunsumerId;
    private final Long maxPrice;

    public AuctionEndEvent(Long auctionId, Long cunsumerId, Long maxPrice) {
        this.auctionId = auctionId;
        this.cunsumerId = cunsumerId;
        this.maxPrice = maxPrice;
    }
}
