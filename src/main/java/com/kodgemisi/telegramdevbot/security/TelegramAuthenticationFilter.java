package com.kodgemisi.telegramdevbot.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on November, 2018
 *
 * @author destan
 */
class TelegramAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	TelegramAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(new AntPathRequestMatcher("/telegram-login", "GET"));
		setAuthenticationManager(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		final TelegramAuthParameters telegramAuthParameters = new TelegramAuthParameters(request);

		final TelegramAuthenticationToken authRequest = new TelegramAuthenticationToken(telegramAuthParameters.toTelegramUser(),
																						telegramAuthParameters.getHash());

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	@Getter
	@Setter
	private class TelegramAuthParameters {

		private final String id;

		private final String first_name;

		private final String last_name;

		private final String username;

		private final String photo_url;

		private final String auth_date;

		private final String hash;

		TelegramAuthParameters(HttpServletRequest request) {
			this.id = request.getParameter("id");
			this.first_name = request.getParameter("first_name");
			this.last_name = request.getParameter("last_name");
			this.username = request.getParameter("username");
			this.photo_url = request.getParameter("photo_url");
			this.auth_date = request.getParameter("auth_date");
			this.hash = request.getParameter("hash");
		}

		TelegramUser toTelegramUser() {
			return new TelegramUser(auth_date, first_name, id, last_name, photo_url, username);
		}

	}
}

