package com.example.auctionmarket.domain.product.dto.response;

import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final Long id;

    private final String productName;

    private final String productContent;

    private final String category;

    private final String soldStatus;

    public ProductResponse(Long id, String productName, String productContent, String category, String soldStatus) {
        this.id = id;
        this.productName = productName;
        this.productContent = productContent;
        this.category = category;
        this.soldStatus = soldStatus;
    }

    public static ProductResponse toDto(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getProductContent(),
                product.getCategory().name(),
                product.getSoldStatus().name()
        );
    }
}
