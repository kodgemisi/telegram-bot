package com.kodgemisi.telegramdevbot;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
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
public class AuthenticationController {

	private final String API_TOKEN;

	AuthenticationController(@Value("${app.telegram-api-token}") String api_token) {
		API_TOKEN = api_token;
	}

	@GetMapping("/form")
	void login(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.getWriter().write("<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<title>Page Title</title>\n" + "</head>\n" + "<body>\n"
										   + "<script async src=\"https://telegram.org/js/telegram-widget.js?5\" data-telegram-login=\"JavaFeedBot\" data-size=\"large\" data-auth-url=\"https://a899613e.ngrok.io/telegram-login\" data-request-access=\"write\"></script>\n"
										   + "<h1>My First Heading</h1>\n" + "<p>My first paragraph.</p>\n" + "\n" + "</body>\n" + "</html>\n");
	}

	@GetMapping
	ResponseEntity<Void> login(AuthRequest authRequest)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

		boolean ok = check(API_TOKEN, authRequest.toDataCheckString(), authRequest.getHash());

		return ok ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	// https://stackoverflow.com/a/36417101/878361
	// https://stackoverflow.com/a/3174887/878361
	private boolean check(String token, String dataCheckString, String hashOriginal) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

		final MessageDigest digest = MessageDigest.getInstance("SHA-256");
		final byte[] encodedhash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

		final SecretKeySpec secretKey = new SecretKeySpec(encodedhash, "HmacSHA256");

		final Mac mac = Mac.getInstance("HMACSHA256");
		mac.init(secretKey);

		final byte[] hash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

		return Hex.encodeHexString(hash).equals(hashOriginal);
	}

}

@Getter
@Setter
class AuthRequest {
	private String id;
	private String first_name;
	private String last_name;
	private String username;
	private String photo_url;
	private String auth_date;
	private String hash;

	/**
	 *
	 * @see <a href="https://core.telegram.org/widgets/login#checking-authorization">Data-check-string</a>
	 */
	String toDataCheckString() {
		final StringBuilder sb = new StringBuilder();
		if(auth_date != null) {
			sb.append("auth_date=");
			sb.append(auth_date);
			sb.append('\n');
		}

		if(first_name != null) {
			sb.append("first_name=");
			sb.append(first_name);
			sb.append('\n');
		}

		if(id != null) {
			sb.append("id=");
			sb.append(id);
			sb.append('\n');
		}

		if(last_name != null) {
			sb.append("last_name=");
			sb.append(last_name);
			sb.append('\n');
		}

		if(photo_url != null) {
			sb.append("photo_url=");
			sb.append(photo_url);
			sb.append('\n');
		}

		if(username != null) {
			sb.append("username=");
			sb.append(username);
		}

		final String result = sb.toString();
		if(result.endsWith("\n")) {
			return result.substring(0, result.length() - 1);
		}
		return result;
	}
}
