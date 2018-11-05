package com.kodgemisi.telegramdevbot.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@Slf4j
class TelegramAuthenticationProvider implements AuthenticationProvider {

	private final String telgramToken;

	TelegramAuthenticationProvider(String telgramToken) {
		this.telgramToken = telgramToken;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final TelegramAuthenticationToken telegramAuthenticationToken = (TelegramAuthenticationToken) authentication;
		final TelegramUser telegramUser = (TelegramUser) telegramAuthenticationToken.getPrincipal();

		if(this.check(telegramUser.toDataCheckString(), telegramAuthenticationToken.getCredentials().toString())) {
			return new TelegramAuthenticationToken(telegramAuthenticationToken.getPrincipal(), telegramAuthenticationToken.getCredentials(), telegramUser.getAuthorities());
		}

		throw new BadCredentialsException("Incorrect hash.");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (TelegramAuthenticationToken.class.isAssignableFrom(authentication));
	}

	// https://stackoverflow.com/a/36417101/878361
	// https://stackoverflow.com/a/3174887/878361
	private boolean check(String dataCheckString, String hashOriginal) {

		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] encodedhash = digest.digest(telgramToken.getBytes(StandardCharsets.UTF_8));

			final SecretKeySpec secretKey = new SecretKeySpec(encodedhash, "HmacSHA256");

			final Mac mac = Mac.getInstance("HMACSHA256");
			mac.init(secretKey);

			final byte[] hash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

			return Hex.encodeHexString(hash).equals(hashOriginal);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
}
