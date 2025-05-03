package com.example.auctionmarket.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.auth.dto.LoginResponse;
import com.example.auctionmarket.domain.auth.dto.SigninRequest;
import com.example.auctionmarket.domain.auth.dto.SignupRequest;
import com.example.auctionmarket.domain.auth.dto.SignupResponse;
import com.example.auctionmarket.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/v1/auth/signup")
	public Response<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
		SignupResponse signup = authService.signup(signupRequest.getEmail(), signupRequest.getPassword(),
			signupRequest.getNickname(), signupRequest.getPhoneNumber(), signupRequest.getRole());
		return Response.of(signup);
	}

	@PostMapping("/v1/auth/signin")
	public Response<LoginResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
		LoginResponse login = authService.signin(signinRequest.getEmail(), signinRequest.getPassword());
		return Response.of(login);
	}

}
