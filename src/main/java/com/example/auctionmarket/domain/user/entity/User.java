package com.example.auctionmarket.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import com.example.auctionmarket.domain.user.enums.Role;
import jakarta.persistence.*;
import org.apache.logging.log4j.message.ParameterizedMessage;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class User extends TimeStamped{

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

	// @OneToOne
	// @JoinColumn(name = "payment_id")
	// private Payment payment;

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

	public String getNickname(){
		return  this.nickname = nickname;
	}

	public void setCouponUser(CouponUser couponUser){
		couponUserList.add(couponUser);
	}

}
