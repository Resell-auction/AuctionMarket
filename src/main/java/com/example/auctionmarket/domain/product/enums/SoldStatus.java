package com.example.auctionmarket.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SoldStatus {
    SOLD(true),
    UNSOLD(false);

    private final boolean status;
}
