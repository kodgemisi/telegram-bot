package com.kodgemisi.telegramdevbot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Set;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Service
@Slf4j
public class MessageService {

	private final RestTemplate restTemplate;

	private final SubscriberRepository subscriberRepository;

	private final UserOperationsService userOperationsService;

	private final String SEND_URL;

	public MessageService(RestTemplate restTemplate, SubscriberRepository subscriberRepository, UserOperationsService userOperationsService,
			@Value("${app.url.send-message}") String sendUrl) {
		this.restTemplate = restTemplate;
		this.subscriberRepository = subscriberRepository;
		this.userOperationsService = userOperationsService;
		SEND_URL = sendUrl;
	}

	/**
	 * @param message
	 * @param userId  telegram user id
	 */
	void sendMessageTo(String message, String userId) {

		final SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(userId);
		sendMessage.setText(message);

		try {
			final ResponseEntity<String> result = restTemplate.postForEntity(SEND_URL, sendMessage, String.class);
			log.info("result {}, {}", result.getStatusCode(), result.getBody());
		}
		catch (Exception e) {// gives 400 when trying to send message to a non-subscribed (not a friend on Telegram) user
			log.error(e.getMessage() + " when trying to send message to userId " + userId, e);
		}
	}

	void sendMessageToAllUsers(Message message) {

		if (!userOperationsService.isAdmin(message.getFrom().getId())) {
			log.warn("/send is used by an unauthorized user {} for message {}", message.getFrom(), message.getText());
			return;
		}

		// TODO ensure there is only one command

		final String text = message.getText().replace("/send ", "");

		if (StringUtils.isBlank(text)) {
			log.info("Message is empty in /send command, ignoring... {}", text);
			return;
		}

		final Set<Subscriber> activeUsers = subscriberRepository.findAllActiveUserIds();
		log.info("activeUsers: {}", activeUsers);

		for (Subscriber subscriber : activeUsers) {
			this.sendMessageTo(text, subscriber.getTelegramId().toString());
		}

	}
}
