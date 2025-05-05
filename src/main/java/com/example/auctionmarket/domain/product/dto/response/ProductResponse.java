package com.example.auctionmarket.domain.product.dto.response;

import java.util.List;

import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.productimage.dto.response.ProductImageResponse;

import lombok.Getter;

@Getter
public class ProductResponse {

    private final Long id;
    private final String productName;
    private final String productContent;
    private final String category;
    private final String soldStatus;
    private final List<ProductImageResponse> images;

    public ProductResponse(Long id, String productName, String productContent, String category, String soldStatus, List<ProductImageResponse> images) {
        this.id = id;
        this.productName = productName;
        this.productContent = productContent;
        this.category = category;
        this.soldStatus = soldStatus;
        this.images = images;
    }

    public static ProductResponse toDto(Product product) {

        List<ProductImageResponse> imageResponses = product.getProductImageList().stream()
                .map(ProductImageResponse::new)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getProductContent(),
                product.getCategory().name(),
                product.getSoldStatus().name(),
                imageResponses
        );
    }
}
