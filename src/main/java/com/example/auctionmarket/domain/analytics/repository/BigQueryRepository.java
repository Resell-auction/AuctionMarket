package com.example.auctionmarket.domain.analytics.repository;

import java.util.List;

import com.example.auctionmarket.domain.analytics.dto.response.DailyAverageBidResponse;
import com.example.auctionmarket.domain.analytics.dto.response.HourlyAverageBidResponse;
import com.example.auctionmarket.domain.product.enums.ProductCategory;


public interface BigQueryRepository {

	List<HourlyAverageBidResponse> findHourlyAverageBid(ProductCategory category, int days);

	List<DailyAverageBidResponse> findDailyAverageBidByCategory(ProductCategory category);
}