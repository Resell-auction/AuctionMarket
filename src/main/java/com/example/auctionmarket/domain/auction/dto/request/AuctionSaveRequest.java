package com.example.auctionmarket.domain.auction.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class AuctionSaveRequest {

    private Long productId;

    private Long minPrice;

    private Long progressTime;
}
