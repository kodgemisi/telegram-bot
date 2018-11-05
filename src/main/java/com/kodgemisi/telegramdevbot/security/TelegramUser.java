package com.kodgemisi.telegramdevbot.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@Getter
public class TelegramUser implements UserDetails {

	private final String authDate;

	private final String firstName;

	private final String id;

	private final String lastName;

	private final String photoUrl;

	private final String username;

	private final Collection<GrantedAuthority> grantedAuthorities;

	TelegramUser(String authDate, String firstName, String id, String lastName, String photoUrl, String username) {
		this.authDate = authDate;
		this.firstName = firstName;
		this.id = id;
		this.lastName = lastName;
		this.photoUrl = photoUrl;
		this.username = username;

		grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("USER");
	}

	/**
	 * @see <a href="https://core.telegram.org/widgets/login#checking-authorization">Data-check-string</a>
	 */
	String toDataCheckString() {
		final StringBuilder sb = new StringBuilder();
		if (authDate != null) {
			sb.append("auth_date=");
			sb.append(authDate);
			sb.append('\n');
		}

		if (firstName != null) {
			sb.append("first_name=");
			sb.append(firstName);
			sb.append('\n');
		}

		if (id != null) {
			sb.append("id=");
			sb.append(id);
			sb.append('\n');
		}

		if (lastName != null) {
			sb.append("last_name=");
			sb.append(lastName);
			sb.append('\n');
		}

		if (photoUrl != null) {
			sb.append("photo_url=");
			sb.append(photoUrl);
			sb.append('\n');
		}

		if (username != null) {
			sb.append("username=");
			sb.append(username);
		}

		final String result = sb.toString();
		if (result.endsWith("\n")) {
			return result.substring(0, result.length() - 1);
		}
		return result;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
