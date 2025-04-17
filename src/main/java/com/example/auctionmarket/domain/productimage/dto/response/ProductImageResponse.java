package com.example.auctionmarket.domain.productimage.dto.response;

import lombok.Getter;

@Getter
public class ProductImageResponse {

    private final Long id;

    private final String fileName;

    private final String originFileName;

    private final String imageUrl;

    public ProductImageResponse(Long id, String fileName, String originFileName, String imageUrl) {
        this.id = id;
        this.fileName = fileName;
        this.originFileName = originFileName;
        this.imageUrl = imageUrl;
    }
}
