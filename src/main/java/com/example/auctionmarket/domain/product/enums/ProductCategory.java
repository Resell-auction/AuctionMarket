package com.example.auctionmarket.domain.product.enums;

import java.util.Arrays;

public enum ProductCategory {
    SHOES,
    CLOTHES,
    BAG,
    LUXURY,
    ACCESSORY,
    BEAUTY;

    public static ProductCategory of(String category) {
        return Arrays.stream(ProductCategory.values())
                .filter(productCategory -> productCategory.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("카테고리가 일치하지 않습니다."));
    }
}

