package com.example.auctionmarket.domain.user.dto;

import java.time.format.DateTimeFormatter;

import com.example.auctionmarket.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class MyPageResponse {
	private Long id;
	private String email;
	private String nickName;
	private String role;
	private String phoneNumber;
	private String createdAt;
	private String modifiedAt;

	public static MyPageResponse from(User user) {
		return MyPageResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.nickName(user.getNickname())
			.role(user.getRole().name())
			.phoneNumber(user.getPhoneNumber())
			.createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.modifiedAt(user.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.build();
	}
}
