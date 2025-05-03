package com.example.auctionmarket.domain.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyAverageBidResponse {

	private Integer hourOfDay;

	private Integer averageWinningBid;
}