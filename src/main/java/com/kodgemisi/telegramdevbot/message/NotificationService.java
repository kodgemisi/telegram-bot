package com.kodgemisi.telegramdevbot.message;

import com.kodgemisi.telegramdevbot.suggestion.Suggestion;
import org.springframework.stereotype.Component;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@Component
public class NotificationService {

	private final MessageService messageService;

	NotificationService(MessageService messageService) {
		this.messageService = messageService;
	}


	public void sendSuggestionStatusUpdateToSuggester(Suggestion suggestion) {
		messageService.sendMessage("Your suggestion [" + suggestion.getText() + "] is " + suggestion.getStatus() + " by admins.",
								   suggestion.getSuggesterId());
	}

	public void sendValidationErrorToSuggester(Integer suggesterId) {
		messageService.sendMessage("You can only suggest a URL! Format is:\n/suggest <url>", suggesterId);
	}

	public void sendSuggestionReceivedNotificationToSuggester(Integer suggesterId) {
		messageService.sendMessage("Your suggestion is delivered to admins.", suggesterId);
	}

	void sendReactivatedMessage(Subscriber existingSubscriber) {
		messageService.sendMessage("Welcome aboard (again)! From now on you will receive interesting technical post links from time to time.", existingSubscriber.getTelegramId());
	}

	void sendActivatedMessage(Subscriber subscriber) {
		messageService.sendMessage("Welcome aboard! From now on you will receive interesting technical post links from time to time.", subscriber.getTelegramId());
	}

	void sendDisabledMessage(Subscriber subscriber) {
		messageService.sendMessage("Your account is disabled. Until you reactivate your account by typing /start you will no longer receive anything from this bot.", subscriber.getTelegramId());
	}
}
