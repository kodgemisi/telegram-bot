package com.kodgemisi.telegramdevbot.message;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
import java.util.stream.Collectors;

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

	private final String SEND_URL;

	private final String EDIT_MARKUP_URL;

	public MessageService(RestTemplate restTemplate, SubscriberRepository subscriberRepository, @Value("${app.url.send-message}") String sendUrl,
			@Value("${app.url.edit-markup-message}") String editMarkupUrl) {
		this.restTemplate = restTemplate;
		this.subscriberRepository = subscriberRepository;
		SEND_URL = sendUrl;
		EDIT_MARKUP_URL = editMarkupUrl;
	}

	/**
	 * @param message
	 * @param userId  telegram user id
	 */
	public ApiResponse sendMessage(String message, Integer userId) {

		final SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(Long.valueOf(userId));
		sendMessage.setText(message);

		return this.sendMessage(sendMessage);
	}

	private ApiResponse sendMessage(SendMessage message) {
		try {
			final ResponseEntity<ApiResponse> result = restTemplate.postForEntity(SEND_URL, message, ApiResponse.class);
			log.info("result {}, {}", result.getStatusCode(), result.getBody());
			return result.getBody();
		}
		catch (HttpClientErrorException e) { // gives 400 when trying to send message to a non-subscribed (not a friend on Telegram) user
			log.error("Error when trying to send message to userId {}. Message: {}", message.getChatId(), e.getResponseBodyAsString());
			log.error(e.getMessage(), e);

			throw e;
		}
	}

	public ApiResponse editMarkupMessage(Map<String, Object> message) {
		try {
			final ResponseEntity<ApiResponse> result = restTemplate.postForEntity(EDIT_MARKUP_URL, message, ApiResponse.class);
			log.info("result {}, {}", result.getStatusCode(), result.getBody());
			return result.getBody();
		}
		catch (HttpClientErrorException e) {
			log.error("Error when trying to edit message. Message: {}. Error is {}", message, e.getResponseBodyAsString());
			log.error(e.getMessage(), e);

			throw e;
		}
	}

	public List<Message> sendToAll(SendMessage message, Collection<String> telegramUserIds) {
		final Collection<ApiResponse> apiResponses = new ArrayList<>();

		for (String telegramId : telegramUserIds) {
			message.setChatId(telegramId);
			final ApiResponse apiResponse = this.sendMessage(message);
			apiResponses.add(apiResponse);
			log.info("messageId: {} chatId {}", apiResponse.getResult().getMessageId(), apiResponse.getResult().getChatId());
		}

		return apiResponses.stream().map(ApiResponse::getResult).collect(Collectors.toList());
	}

	/**
	 * Called when {@code /send} command is used.
	 */
	void sendMessageToAllUsers(Message message) {

		if (!this.isAdmin(message.getFrom().getId())) {
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
			this.sendMessage(text, subscriber.getTelegramId());
		}

	}

	private boolean isAdmin(Integer telegramId) {
		final Optional<Subscriber> admin = subscriberRepository.findAdminByTelegramId(telegramId);
		return admin.isPresent();
	}
}


