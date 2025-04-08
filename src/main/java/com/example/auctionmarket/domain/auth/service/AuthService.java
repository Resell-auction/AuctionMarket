package com.example.auctionmarket.domain.auth.service;

import java.util.Objects;
import java.util.Optional;

import com.example.auctionmarket.domain.user.enums.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auctionmarket.domain.auth.dto.LoginResponse;
import com.example.auctionmarket.domain.auth.dto.SignupResponse;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.exception.AlreadyExistsEmailException;
import com.example.auctionmarket.domain.user.exception.EmailAccessDeniedException;
import com.example.auctionmarket.domain.user.exception.InvalidPasswordException;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import com.example.auctionmarket.global.jwt.JwtUtil;
import com.example.auctionmarket.domain.user.exception.EmailNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;

	public SignupResponse signup(String email, String password, String nickname, String phoneNumber, String userRole) {
		if (userRepository.existsByEmail(email)) {
			throw new AlreadyExistsEmailException();
		}

		String encodedPassword = bCryptPasswordEncoder.encode(password);
		User User = new User(email, encodedPassword, nickname, phoneNumber, UserRole.of(userRole));
		User saveUser = userRepository.save(User);

		String accessToken = jwtUtil.createAccessToken(saveUser.getId(), saveUser.getEmail(), saveUser.getRole(), saveUser.getNickname());
		String refreshToken = jwtUtil.createRefreshToken(saveUser.getId());
		saveUser.updateRefreshToken(refreshToken);
		userRepository.save(saveUser);
		return new SignupResponse(accessToken, refreshToken);
	}

	public LoginResponse signin(String email, String password) {
		User user = userRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
		Optional.ofNullable(user.getDeletedAt())
			.filter(Objects::nonNull)
			.ifPresent(deletedAt -> {
				throw new EmailAccessDeniedException();
			});

		if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException();
		}
		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole(), user.getNickname());

		String refreshToken = jwtUtil.createRefreshToken(user.getId());
		user.updateRefreshToken(refreshToken);
		userRepository.save(user);
		return new LoginResponse(accessToken, refreshToken);
	}

}
