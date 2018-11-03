package com.kodgemisi.telegramdevbot.web;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Controller
@RequestMapping("/telegram-login")
class AuthenticationController {

	private final String API_TOKEN;

	private final String CALL_BACK_URL;

	AuthenticationController(@Value("${app.telegram-api-token}") String apiToken, @Value("${app.base-url}") String baseUrl) {
		API_TOKEN = apiToken;
		CALL_BACK_URL = baseUrl + "/telegram-login";
	}

	@GetMapping("/form")
	String login(Model model) throws IOException {
		model.addAttribute("baseUrl", CALL_BACK_URL);
		return "telegram-login";
	}

	@GetMapping
	ResponseEntity<Void> login(AuthRequest authRequest) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

		boolean ok = check(API_TOKEN, authRequest.toDataCheckString(), authRequest.getHash());

		return ok ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	// https://stackoverflow.com/a/36417101/878361
	// https://stackoverflow.com/a/3174887/878361
	private boolean check(String token, String dataCheckString, String hashOriginal)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

		final MessageDigest digest = MessageDigest.getInstance("SHA-256");
		final byte[] encodedhash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

		final SecretKeySpec secretKey = new SecretKeySpec(encodedhash, "HmacSHA256");

		final Mac mac = Mac.getInstance("HMACSHA256");
		mac.init(secretKey);

		final byte[] hash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

		return Hex.encodeHexString(hash).equals(hashOriginal);
	}
}