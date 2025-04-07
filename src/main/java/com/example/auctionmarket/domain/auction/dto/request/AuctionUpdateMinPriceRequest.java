package com.example.auctionmarket.domain.auction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionUpdateMinPriceRequest {

    private Long minPrice;
}
