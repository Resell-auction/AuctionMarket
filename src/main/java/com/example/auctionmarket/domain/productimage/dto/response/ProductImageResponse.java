package com.example.auctionmarket.domain.productimage.dto.response;

import lombok.Getter;

@Getter
public class ProductImageResponse {

    private final Long id;

    private final String fileName;

    private final String originFileName;

    private final String s3ImageUrl;

    private final String cloudFrontImageUrl;

    public ProductImageResponse(Long id, String fileName, String originFileName, String s3ImageUrl, String cloudFrontImageUrl) {
        this.id = id;
        this.fileName = fileName;
        this.originFileName = originFileName;
        this.s3ImageUrl = s3ImageUrl;
        this.cloudFrontImageUrl = cloudFrontImageUrl;
    }
}
