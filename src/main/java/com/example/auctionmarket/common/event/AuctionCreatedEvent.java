package com.example.auctionmarket.common.event;

import com.example.auctionmarket.domain.auction.entity.Auction;
import lombok.Getter;

@Getter
public class AuctionCreatedEvent {
    private final Auction auction;

    public AuctionCreatedEvent(Auction auction) {
        this.auction = auction;
    }
}
