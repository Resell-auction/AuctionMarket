package com.example.auctionmarket.global.redis;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshCacheUtil {

	private final RefreshTokenRepository refreshTokenRepository;

	@Cacheable(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public String saveRefreshToken(String token, String userId) {
		return token;
	}

	@Cacheable(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public String getRefreshToken(String userId) {
		RefreshToken refreshToken = refreshTokenRepository.findById(userId)
			.orElseThrow(AuthenticationExpiredException::new);
		return refreshToken.getToken();
	}

	@CacheEvict(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public void deleteRefreshToken(String userId) {
		refreshTokenRepository.deleteById(userId);
	}

	@CachePut(value = "refresh", key = "#userId", cacheManager = "redisCache")
	public String reissueRefreshToken(String token, String userId) {
		deleteRefreshToken(userId);
		return token;
	}
}
