package com.example.auctionmarket.domain.auth.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auctionmarket.domain.auth.dto.SignupResponse;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.exception.AlreadyExistsEmailException;
import com.example.auctionmarket.domain.user.exception.EmailAccessDeniedException;
import com.example.auctionmarket.domain.user.exception.InvalidPasswordException;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import com.example.auctionmarket.global.jwt.JwtUtil;
import com.example.auctionmarket.domain.user.exception.EmailNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;

	public SignupResponse signup(String email, String password, String nickname, String phoneNumber) {
		if (userRepository.existsByEmail(email)) {
			throw new AlreadyExistsEmailException();
		}

		String encodedPassword = bCryptPasswordEncoder.encode(password);
		User User = new User(email, encodedPassword, nickname, phoneNumber);
		User saveUser = userRepository.save(User);

		String bearerToken = jwtUtil.createAccessToken(saveUser.getId(), saveUser.getEmail(), saveUser.getRole(), saveUser.getNickname());

//SignupResponse.from(saveUser);
//
		return new SignupResponse(bearerToken);

		return SignupResponse.from(saveUser);
	}

	public void signin(String email, String password, HttpServletResponse servletResponse) {
		User user = userRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
		Optional.ofNullable(user.getDeletedAt())
			.filter(Objects::nonNull)
			.ifPresent(deletedAt -> {
				throw new EmailAccessDeniedException();
			});

		if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException();
		}

		createAndSaveJwt(user, servletResponse);
	}

	private void createAndSaveJwt(User user, HttpServletResponse servletResponse) {
		System.out.println("test:"+ user.getId()+ user.getEmail()+ user.getRole()+user.getNickname());
		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole(),
			user.getNickname());
		jwtUtil.accessTokenSetHeader(accessToken, servletResponse);

		String refreshToken = jwtUtil.createRefreshToken(user.getId());
		jwtUtil.refreshTokenSetCookie(refreshToken, servletResponse);
		user.updateRefreshToken(refreshToken);
		userRepository.save(user);
	}
}
