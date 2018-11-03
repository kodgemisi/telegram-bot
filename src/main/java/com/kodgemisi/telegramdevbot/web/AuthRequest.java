package com.kodgemisi.telegramdevbot.web;

import lombok.Getter;
import lombok.Setter;

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
	 * @see <a href="https://core.telegram.org/widgets/login#checking-authorization">Data-check-string</a>
	 */
	String toDataCheckString() {
		final StringBuilder sb = new StringBuilder();
		if (auth_date != null) {
			sb.append("auth_date=");
			sb.append(auth_date);
			sb.append('\n');
		}

		if (first_name != null) {
			sb.append("first_name=");
			sb.append(first_name);
			sb.append('\n');
		}

		if (id != null) {
			sb.append("id=");
			sb.append(id);
			sb.append('\n');
		}

		if (last_name != null) {
			sb.append("last_name=");
			sb.append(last_name);
			sb.append('\n');
		}

		if (photo_url != null) {
			sb.append("photo_url=");
			sb.append(photo_url);
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
}