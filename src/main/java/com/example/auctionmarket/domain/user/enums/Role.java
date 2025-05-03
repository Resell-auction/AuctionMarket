package com.example.auctionmarket.domain.user.enums;

import java.util.Arrays;
import com.example.auctionmarket.domain.user.exception.InvalidUserRoleException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	ADMIN(Authority.ADMIN),
	USER(Authority.USER);
	private final String role;

	public static Role of(String role) {
		return Arrays.stream(Role.values())
			.filter(r -> r.name().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new InvalidUserRoleException());
	}

	public static class Authority {
		public static final String ADMIN = "ADMIN";
		public static final String USER = "USER";
	}
}

