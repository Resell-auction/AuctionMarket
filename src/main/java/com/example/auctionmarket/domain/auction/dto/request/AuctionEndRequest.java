package com.example.auctionmarket.domain.auction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionEndRequest {
    private Long auctionId;
    private Long consumerId;
    private Long amount;
}
