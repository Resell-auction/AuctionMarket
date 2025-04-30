package com.example.auctionmarket.domain.analytics.dto.request;

import com.example.auctionmarket.domain.product.enums.ProductCategory;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyAverageBidRequest {

	@NotNull(message = "카테고리를 반드시 입력해주세요")
	private ProductCategory category;

}
