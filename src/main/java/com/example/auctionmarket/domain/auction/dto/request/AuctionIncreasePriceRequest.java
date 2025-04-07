package com.example.auctionmarket.domain.auction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class AuctionIncreasePriceRequest {

    @NotNull
    private Long increasePrice;
}
