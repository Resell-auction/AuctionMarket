package com.example.auctionmarket.domain.user.dto;

import java.time.format.DateTimeFormatter;

import com.example.auctionmarket.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
	private String email;
	private String nickName;
	private String userRole;
	private String phoneNumber;
	private String modifiedAt;

	public static UserResponse from(User user) {
		return UserResponse.builder()
			.email(user.getEmail())
			.nickName(user.getNickname())
			.userRole(user.getUserRole().name())
			.phoneNumber(user.getPhoneNumber())
			.modifiedAt(user.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.build();
	}
}
