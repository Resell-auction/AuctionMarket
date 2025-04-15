package com.example.auctionmarket.domain.productimage.dto.response;

import lombok.Getter;

@Getter
public class ProductImageResponse {

    private final Long id;

    private final String imagePath;

    private final String fileName;

    public ProductImageResponse(Long id, String imagePath, String fileName) {
        this.id = id;
        this.imagePath = imagePath;
        this.fileName = fileName;
    }
}
