package com.kodgemisi.telegramdevbot.suggestion;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@Getter
@Setter
@ToString
public class Suggestion {

	@Id
	private String id;

	/**
	 * The user who made the suggestion
	 */
	private Integer suggesterId;

	private String suggesterFullName;

	/**
	 * The admin who accepted or declined the suggestion
	 */
	private Integer responsibleAdminId;

	private String responsibleAdminFullName;

	/**
	 * Suggested text
	 */
	private String text;

	private ChatIdentifier suggestionChatMessage;

	private Set<ChatIdentifier> receivedPromptsByAdmins;

	private LocalDateTime suggestionDate;

	private LocalDateTime actionDate;

	private SuggestionStatus status;

	private Suggestion() {
		this.status = SuggestionStatus.PENDING;
		this.receivedPromptsByAdmins = new HashSet<>();
		this.suggestionDate = LocalDateTime.now();
	}

	Suggestion(Message message) {
		this();

		final String text = message.getText().replace("/suggest ", "").trim();

		if(StringUtils.isBlank(text)) {
			throw new IllegalArgumentException("Suggestion text cannot be blank. It was " + message.getText());
		}

		// URL validation
		try {
			final URI uri = new URI(text);

			if ("mailto".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null ) {
				throw new URISyntaxException(text, "");
			}

		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(text + " is not an URL");
		}

		this.setText(text);
		this.setSuggesterId(message.getFrom().getId());
		this.setSuggestionChatMessage(message);
		this.setSuggesterFullName(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
	}

	public void acceptedBy(User admin) {
		prepareForStatusChange(admin);
		this.status = SuggestionStatus.ACCEPTED;
	}

	public void declinedBy(User admin) {
		prepareForStatusChange(admin);
		this.status = SuggestionStatus.DECLINED;
	}

	private void prepareForStatusChange(User admin) {
		this.actionDate = LocalDateTime.now();
		this.responsibleAdminId = admin.getId();
		this.responsibleAdminFullName = admin.getFirstName() + " " + admin.getLastName();
	}

	private void setSuggestionChatMessage(Message message) {
		this.suggestionChatMessage = new ChatIdentifier();
		this.suggestionChatMessage.setChatId(message.getChatId().toString());
		this.suggestionChatMessage.setMessageId(message.getMessageId().toString());
	}

	String getPromptText() {
		return "There is a suggestion from " + this.getSuggesterFullName() + "\n" + this.getText();
	}

	enum SuggestionStatus {
		PENDING("Pending"), ACCEPTED("Accepted"), DECLINED("Declined");

		private String userFriendlyText;

		SuggestionStatus(String userFriendlyText) {
			this.userFriendlyText = userFriendlyText;
		}

		@Override
		public String toString() {
			return userFriendlyText;
		}
	}

}