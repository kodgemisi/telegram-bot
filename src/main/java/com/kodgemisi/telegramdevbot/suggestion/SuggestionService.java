package com.kodgemisi.telegramdevbot.suggestion;

import com.kodgemisi.telegramdevbot.message.Subscriber;
import com.kodgemisi.telegramdevbot.message.SubscriberRepository;
import com.kodgemisi.telegramdevbot.message.MessageService;
import com.kodgemisi.telegramdevbot.message.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@Service
@Slf4j
public class SuggestionService {

	private final MessageService messageService;

	private final SuggestionRepository suggestionRepository;

	private final SubscriberRepository subscriberRepository;

	private final NotificationService notificationService;

	SuggestionService(MessageService messageService, SuggestionRepository suggestionRepository, SubscriberRepository subscriberRepository,
			 NotificationService notificationService) {
		this.messageService = messageService;
		this.suggestionRepository = suggestionRepository;
		this.subscriberRepository = subscriberRepository;
		this.notificationService = notificationService;
	}

	public void createSuggestion(Message message) {
		final Suggestion suggestion;
		try {
			suggestion = new Suggestion(message);
		}
		catch (IllegalArgumentException e) {
			notificationService.sendValidationErrorToSuggester(message.getFrom().getId());
			return;
		}
		suggestionRepository.save(suggestion);

		this.sendSuggestionToAdmins(suggestion);

		notificationService.sendSuggestionReceivedNotificationToSuggester(suggestion.getSuggesterId());
	}

	public void acceptSuggestion(String suggestionId, User admin) {
		final Suggestion suggestion = suggestionRepository.findById(suggestionId).orElseThrow(IllegalStateException::new);
		suggestion.acceptedBy(admin);
		suggestionRepository.save(suggestion);

		sendSuggestionToUsers(suggestion);

		sendSuggestionFeedbackToAdmins(suggestion);

		notificationService.sendSuggestionStatusUpdateToSuggester(suggestion);
	}

	public void declineSuggestion(String suggestionId, User admin) {
		final Suggestion suggestion = suggestionRepository.findById(suggestionId).orElseThrow(IllegalStateException::new);
		suggestion.declinedBy(admin);
		suggestionRepository.save(suggestion);

		sendSuggestionFeedbackToAdmins(suggestion);

		notificationService.sendSuggestionStatusUpdateToSuggester(suggestion);
	}

	private void sendSuggestionFeedbackToAdmins(Suggestion suggestion) {
		final Map<String, Object> sendMessage = new HashMap<>();
		sendMessage.put("reply_markup", getInlineKeyboardMarkupForAdminFeedback(suggestion));

		for (ChatIdentifier chatIdentifier : suggestion.getReceivedPromptsByAdmins()) {
			sendMessage.put("chat_id", chatIdentifier.getChatId());
			sendMessage.put("message_id", chatIdentifier.getMessageId());
			messageService.editMarkupMessage(sendMessage);
		}
	}

	private void sendSuggestionToAdmins(Suggestion suggestion) {
		final SendMessage sendMessage = new SendMessage();
		sendMessage.setReplyMarkup(getInlineKeyboardMarkup(suggestion.getId()));
		sendMessage.setText(suggestion.getPromptText());

		final Set<Subscriber> admins = subscriberRepository.findAllAdmins();

		final Set<String> adminIds = admins // @formatter:off
				.stream()
				.map(subscriber -> subscriber.getTelegramId().toString())
				.collect(Collectors.toSet()); // @formatter:on

		final Collection<Message> apiResponses = messageService.sendToAll(sendMessage, adminIds);

		final Set<ChatIdentifier> chatIdentifiers = apiResponses // @formatter:off
				.stream()
				.map(ChatIdentifier::new)
				.collect(Collectors.toSet()); // @formatter:on

		suggestion.setReceivedPromptsByAdmins(chatIdentifiers);

		suggestionRepository.save(suggestion);
	}

	private void sendSuggestionToUsers(Suggestion suggestion) {
		final SendMessage sendMessage = new SendMessage();
		sendMessage.setText(suggestion.getText());

		final Set<Subscriber> allUsersExceptAdmins = subscriberRepository.findAllActiveRegularUsers();
		final Set<String> userIds = allUsersExceptAdmins // @formatter:off
				.stream()
				.map(subscriber -> subscriber.getTelegramId().toString())
				.collect(Collectors.toSet()); // @formatter:on

		// Remove suggester's own id to avoid send their own link to themselves again.
		userIds.remove(suggestion.getSuggesterId().toString());

		messageService.sendToAll(sendMessage, userIds);
	}

	private InlineKeyboardMarkup getInlineKeyboardMarkup(String suggestionId) {
		final InlineKeyboardButton keyboardButtonAccept = new InlineKeyboardButton("Accept");
		keyboardButtonAccept.setCallbackData("accept," + suggestionId);

		final InlineKeyboardButton keyboardButtonDecline = new InlineKeyboardButton("Decline");
		keyboardButtonDecline.setCallbackData("decline," + suggestionId);

		final List<InlineKeyboardButton> buttons = Arrays.asList(keyboardButtonDecline, keyboardButtonAccept);
		final List<List<InlineKeyboardButton>> buttonRow = Collections.singletonList(buttons);

		final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		inlineKeyboardMarkup.setKeyboard(buttonRow);

		final InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
		replyKeyboard.setKeyboard(buttonRow);
		return replyKeyboard;
	}

	private InlineKeyboardMarkup getInlineKeyboardMarkupForAdminFeedback(Suggestion suggestion) {
		final InlineKeyboardButton keyboardButtonResult = new InlineKeyboardButton(
				suggestion.getStatus() + " by " + suggestion.getResponsibleAdminFullName());
		keyboardButtonResult.setUrl(suggestion.getText());

		final List<InlineKeyboardButton> buttons = Collections.singletonList(keyboardButtonResult);
		final List<List<InlineKeyboardButton>> buttonRow = Collections.singletonList(buttons);

		final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		inlineKeyboardMarkup.setKeyboard(buttonRow);

		final InlineKeyboardMarkup replyKeyboard = new InlineKeyboardMarkup();
		replyKeyboard.setKeyboard(buttonRow);
		return replyKeyboard;
	}
}
