package com.kodgemisi.telegramdevbot.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created on November, 2018
 *
 * @author destan
 */
class TelegramAuthenticationToken extends AbstractAuthenticationToken {

	private final Object credentials;

	private final Object principal;

	TelegramAuthenticationToken(Object principal, Object credentials) {
		super(null);
		this.credentials = credentials;
		this.principal = principal;
	}

	TelegramAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.credentials = credentials;
		this.principal = principal;
		super.setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}
}
