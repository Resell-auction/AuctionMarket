package com.example.auctionmarket.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&]).{8,}$"
		, message = "대문자, 숫자, 특수문자(!,@,#,$,%,^,&)를 최소 1개 이상 포함한 8자리 이상으로 입력해주세요.")
	private String password;

	@NotBlank
	private String nickname;

	@NotBlank
	@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 양식에 맞지 않습니다. ex) 010-1234-5678")
	private String phoneNumber;

	@NotBlank
	private String role;

	public SignupRequest(String email, String password, String nickname, String phoneNumber, String role) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
	}
}
