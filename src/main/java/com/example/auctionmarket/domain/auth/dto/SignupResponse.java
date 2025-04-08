package com.example.auctionmarket.domain.auth.dto;

import java.time.format.DateTimeFormatter;

import com.example.auctionmarket.domain.user.entity.User;

import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Getter;

@Getter
//@Builder
public class SignupResponse {

	private final String accessToken;
	private final String refreshToken;

	public SignupResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

}
