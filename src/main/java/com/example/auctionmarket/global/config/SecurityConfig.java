package com.example.auctionmarket.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.auctionmarket.global.filter.ExceptionHandlerFilter;
import com.example.auctionmarket.global.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final ExceptionHandlerFilter exceptionHandlerFilter;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 커스텀 필터는 계속 주석 처리 (테스트 목적)
			// .addFilterBefore(jwtAuthenticationFilter, SecurityContextHolderAwareRequestFilter.class)
			// .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
			.formLogin(AbstractHttpConfigurer::disable)
			.anonymous(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.rememberMe(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				// *** 여기를 원래대로 또는 로그인 경로를 포함하도록 수정 ***
				.requestMatchers(
					// 예시: 원래 패턴으로 되돌리기
					new AntPathRequestMatcher("/api/*/auth/**"),
					// 또는 더 명시적으로
					new AntPathRequestMatcher("/api/v1/auth/signin"),
					// --- 다른 permitAll 경로들 ---
					new AntPathRequestMatcher("/v1/auctions/end"),
					new AntPathRequestMatcher("/v2/auctions/**"), // 중복된 것 같으니 하나는 제거해도 될 수 있음
					new AntPathRequestMatcher("/v1/analytics/**"),
					// new AntPathRequestMatcher("/v2/auctions/**"), // 중복
					new AntPathRequestMatcher("/v3/auctions/**")
					// ---------------------------
				).permitAll()
				.anyRequest().authenticated()
			)
			.build();
	}

}
