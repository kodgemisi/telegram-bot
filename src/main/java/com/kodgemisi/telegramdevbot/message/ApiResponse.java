package com.kodgemisi.telegramdevbot.message;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
class ApiResponse {
	private boolean ok;
	private Message result;
}