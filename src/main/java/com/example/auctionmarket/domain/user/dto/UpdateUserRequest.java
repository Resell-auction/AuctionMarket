package com.example.auctionmarket.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateUserRequest {
	private String nickname;
	private String phoneNumber;

}
