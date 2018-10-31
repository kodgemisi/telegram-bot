package com.kodgemisi.telegramdevbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
class WebHookDispatcher {

	private final UserOperationsService userOperationsService;

	private final MessageService messageService;

	WebHookDispatcher(UserOperationsService userOperationsService, MessageService messageService) {
		this.userOperationsService = userOperationsService;
		this.messageService = messageService;
	}

	@Async
	public void dispatch(Update update) {

		final List<MessageEntity> entities = getEntities(update.getMessage());

		if (entities.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Update discarded because it has no command. updateId: {}", update.getUpdateId());
			}
			return;
		}

		final String command = getFirstCommand(entities);

		switch (command) {
		case "/start":
			userOperationsService.registerOrActivateUser(update.getMessage().getFrom());
			return;
		case "/stop":
			userOperationsService.disableUser(update.getMessage().getFrom());
			return;
		case "/send":
			messageService.sendMessageToAllUsers(update.getMessage());
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
