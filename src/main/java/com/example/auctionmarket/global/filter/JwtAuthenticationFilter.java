package com.example.auctionmarket.global.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.auth.exception.ExpiredJwtTokenException;
import com.example.auctionmarket.domain.auth.exception.InvalidJwtSignatureException;
import com.example.auctionmarket.domain.auth.exception.InvalidTokenException;
import com.example.auctionmarket.domain.auth.exception.UnsupportedJwtTokenException;
import com.example.auctionmarket.domain.user.enums.UserRole;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import com.example.auctionmarket.global.jwt.JwtAuthenticationToken;
import com.example.auctionmarket.global.jwt.JwtUtil;
import com.example.auctionmarket.domain.auth.exception.TokenNotFoundException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String access = jwtUtil.substringToken(authorizationHeader);

			String refresh = request.getHeader("Refresh-Token");
			if (refresh == null){
				throw new TokenNotFoundException();
			}

			try {
				Claims claims = jwtUtil.extractClaims(access);

				if (claims == null) {
					throw new InvalidTokenException();
				}

				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					setAuthentication(claims);
				}

				Long id = Long.parseLong(claims.getSubject());
				request.setAttribute("userId", id);
				request.setAttribute("email", claims.get("email"));
				request.setAttribute("userRole", claims.get("userRole"));

				// Redis 대신 데이터베이스에서 리프레시 토큰 조회
				String storedRefreshToken = userRepository.findRefreshTokenById(id)
					.orElseThrow(() -> new TokenNotFoundException());

				// jwt 토큰 만료 시간 검증
				expiredJwtToken(access, refresh, storedRefreshToken, response);

			} catch (SecurityException | MalformedJwtException e) {
				log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
				throw new InvalidJwtSignatureException();
			} catch (UnsupportedJwtException e) {
				log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
				throw new UnsupportedJwtTokenException();
			} catch (Exception e) {
				log.error("Internal server error", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		filterChain.doFilter(request, response);
	}

	private void setAuthentication(Claims claims) {
		Long suerId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		UserRole userRole = UserRole.of(claims.get("userRole", String.class));
		String nickname = claims.get("nickname", String.class);

		AuthUser authUser = new AuthUser(suerId, email, userRole, nickname);
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	private void expiredJwtToken(String accessToken, String refreshToken, String storedRefreshToken, HttpServletResponse response) throws IOException {
		if (!refreshToken.equals(storedRefreshToken)) {
			throw new ExpiredJwtTokenException();
		}

		boolean isAccessTokenExpired = jwtUtil.isTokenExpired(accessToken);
		boolean isRefreshTokenExpired = jwtUtil.isTokenExpired(refreshToken);


		if (isAccessTokenExpired && isRefreshTokenExpired) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.");
			throw new ExpiredJwtTokenException();
		} else if (isAccessTokenExpired) {
			String newAccessToken = jwtUtil.validateRefreshToken(refreshToken);
			jwtUtil.accessTokenSetHeader(newAccessToken, response);
			jwtUtil.reissueRefreshToken(newAccessToken, response);
		} else if (isRefreshTokenExpired) {
			jwtUtil.reissueRefreshToken(accessToken, response);
		}
	}
}
