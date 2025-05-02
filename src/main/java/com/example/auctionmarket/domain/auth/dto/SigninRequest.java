package com.example.auctionmarket.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SigninRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

}
