package com.example.auctionmarket.domain.analytics.dto.request;

import com.example.auctionmarket.domain.product.enums.ProductCategory;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HourlyAverageBidRequest {

	@NotNull(message = "카테고리를 반드시 입력해주세요")
	private ProductCategory category;

	@Min(value = 1, message = "조회 기간은 최소 1일 이상이어야 합니다.")
	@Max(value = 90, message = "조회 기간은 최대 90일을 초과할 수 없습니다.")
	private Integer days;
}