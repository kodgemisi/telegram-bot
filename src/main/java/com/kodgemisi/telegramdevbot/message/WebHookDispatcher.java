package com.kodgemisi.telegramdevbot.message;

import com.kodgemisi.telegramdevbot.suggestion.SuggestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Service
@Slf4j
public class WebHookDispatcher {

	private final SubscriberService subscriberService;

	private final MessageService messageService;

	private final SuggestionService suggestionService;

	WebHookDispatcher(SubscriberService subscriberService, MessageService messageService, SuggestionService suggestionService) {
		this.subscriberService = subscriberService;
		this.messageService = messageService;
		this.suggestionService = suggestionService;
	}

	@Async
	public void dispatch(Update update) {

		if(update.hasMessage()) {
			dispatch(update.getMessage());
		}
		else if (update.hasCallbackQuery()) {
			dispatch(update.getCallbackQuery());
		}

	}

	private void dispatch(Message message) {
		final List<MessageEntity> entities = getEntities(message);

		if (entities.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Update discarded because it has no command. messageId: {}", message.getMessageId());
			}
			return;
		}

		final String command = getFirstCommand(entities);

		switch (command) {
		case "/start":
			subscriberService.registerOrActivateUser(message.getFrom());
			return;
		case "/stop":
			subscriberService.disableUser(message.getFrom());
			return;
		case "/send":
			messageService.sendMessageToAllUsers(message);
			return;
		case "/suggest":
			suggestionService.createSuggestion(message);
			return;
		}
	}

	private void dispatch(CallbackQuery callbackQuery) {
		final String data = callbackQuery.getData();

		if (data.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("CallbackQuery discarded because it has no data. messageId: {}", callbackQuery.getId());
			}
			return;
		}

		final String[] dataContent = data.split(",");

		switch (dataContent[0]) {
		case "accept":
			suggestionService.acceptSuggestion(dataContent[1], callbackQuery.getFrom());
			return;
		case "decline":
			suggestionService.declineSuggestion(dataContent[1], callbackQuery.getFrom());
			return;
		}
	}

	private List<MessageEntity> getEntities(Message message) {
		final List<MessageEntity> entities = message.getEntities();
		if (entities == null || entities.isEmpty()) {
			return Collections.emptyList();
		}
		return entities;
	}

	private String getFirstCommand(List<MessageEntity> entities) {
		final MessageEntity entity = entities.get(0);
		return entity.getType().equals("bot_command") ? entity.getText() : "";
	}

}
