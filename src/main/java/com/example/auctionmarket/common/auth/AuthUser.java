package com.example.auctionmarket.common.auth;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.auctionmarket.domain.user.enums.Role;
import lombok.Getter;

@Getter
public class AuthUser {

	private final Long id;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;
	private final String nickname;

	public AuthUser(Long id, String email, Role role, String nickname) {
		this.id = id;
		this.email = email;
		this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
		this.nickname = nickname;
	}
}
