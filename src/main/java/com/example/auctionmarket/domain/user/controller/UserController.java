package com.example.auctionmarket.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.user.dto.MyPageResponse;
import com.example.auctionmarket.domain.user.dto.UpdatePasswordRequest;
import com.example.auctionmarket.domain.user.dto.UpdateUserRequest;
import com.example.auctionmarket.domain.user.dto.UserResponse;
import com.example.auctionmarket.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// 프로필 확인
	@GetMapping("/v1/users/my")
	public Response<MyPageResponse> getMyPage(@AuthenticationPrincipal AuthUser authUser) {
		return Response.of(userService.getMyPage(authUser.getId()));
	}

	// 유저정보 수정
	@PatchMapping("/v1/users/my")
	public Response<UserResponse> updateUser(@AuthenticationPrincipal AuthUser authUser,
		@RequestBody UpdateUserRequest updateUserRequest) {
		return Response.of(
			userService.updateUser(authUser.getId(), updateUserRequest.getNickname(),
				updateUserRequest.getPhoneNumber()));
	}

	// 비밀번호 수정
	@PatchMapping("/v1/users/my/password")
	public Response<Void> updatePassword(@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
		userService.updatePassword(authUser.getId(), updatePasswordRequest.getOldPassword(),
			updatePasswordRequest.getNewPassword());
		return Response.empty();
	}

	// 유저 삭제
	@DeleteMapping("/v1/users/my")
	public Response<Void> deleteUser(@AuthenticationPrincipal AuthUser authUser) {
		userService.deleteUser(authUser.getId());
		return Response.empty();
	}
}
