package com.example.auctionmarket.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidResponse {
	private String fieldName;
	private String message;

	public static ValidResponse of(String fieldName, String message) {
		return ValidResponse.builder().fieldName(fieldName).message(message).build();
	}
}
