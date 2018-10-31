package com.kodgemisi.telegramdevbot;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.telegram.telegrambots.meta.api.objects.User;

@Getter
@Setter
@ToString
public class Subscriber {

	@Id
	private String id;

	private String userName;

	private String firstName;

	private String lastName;

	private Integer telegramId;

	private String lang;

	private boolean active;

	private Boolean admin;

	public Subscriber() {
	}

	public Subscriber(User user) {
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.userName = user.getUserName();
		this.telegramId = user.getId();
		this.lang = user.getLanguageCode();
		this.active = true;
	}

	void disable() {
		this.active = false;
	}

	void activate() {
		this.active = true;
	}
}