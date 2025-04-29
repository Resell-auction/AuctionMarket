package com.example.auctionmarket.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductUpdateRequest {

    @NotBlank(message = "물건 이름은 필수 입력값입니다.")
    private String productName;

    private String productContent;

    @NotBlank(message = "카테고리는 필수 입력값입니다.")
    private String category;

    public ProductUpdateRequest(){}
}
