package com.example.auctionmarket.domain.auction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionIncreasePriceRequest {

    @NotNull
    private Long increasePrice;
}
