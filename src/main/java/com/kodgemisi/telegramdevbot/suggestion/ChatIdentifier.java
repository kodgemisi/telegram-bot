package com.kodgemisi.telegramdevbot.suggestion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
@NoArgsConstructor
class ChatIdentifier {

	private String chatId;

	private String messageId;

	public ChatIdentifier(Message message) {
		this.chatId = message.getChatId().toString();
		this.messageId = message.getMessageId().toString();
	}
}