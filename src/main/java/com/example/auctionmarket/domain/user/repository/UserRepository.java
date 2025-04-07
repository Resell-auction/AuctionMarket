package com.example.auctionmarket.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.auctionmarket.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	@Query("SELECT u.refreshToken FROM User u WHERE u.id = :id")
	Optional<String> findRefreshTokenById(@Param("id") Long id);
}
