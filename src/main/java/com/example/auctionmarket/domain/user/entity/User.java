package com.example.auctionmarket.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import org.apache.logging.log4j.message.ParameterizedMessage;

import com.example.auctionmarket.common.entity.BaseEntity;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.user.enums.Role;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;
	private String password;
	private String nickname;

	@Column(name = "refresh_token")
	private String refreshToken;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<CouponUser> couponUserList = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Nullable
	private LocalDateTime deletedAt;

	public User() {
	}

	public User(String email, String password, String nickname, String phoneNumber, Role role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updatePhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void cancelUser() {
		this.deletedAt = LocalDateTime.now();
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
