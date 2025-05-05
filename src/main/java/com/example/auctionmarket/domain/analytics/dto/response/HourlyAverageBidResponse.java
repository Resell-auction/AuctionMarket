package com.example.auctionmarket.domain.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HourlyAverageBidResponse {

	private Integer hourOfDay;

	private Integer averageWinningBid;
}