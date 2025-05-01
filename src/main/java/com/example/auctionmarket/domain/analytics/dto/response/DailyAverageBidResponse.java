package com.example.auctionmarket.domain.analytics.dto.response;

import java.time.LocalDate;

import com.example.auctionmarket.domain.product.enums.ProductCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyAverageBidResponse {

	private LocalDate auctionDate;
	private double averageWinningBid;
}
