package com.kodgemisi.telegramdevbot;

import com.kodgemisi.telegramdevbot.message.WebHookDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Controller
@RequestMapping("/webhook")
@Slf4j
class WebHookController {

	private final String API_TOKEN;

	private final WebHookDispatcher webHookDispatcher;

	WebHookController(@Value("${app.telegram-api-token}") String api_token, WebHookDispatcher webHookDispatcher) {
		API_TOKEN = api_token;
		this.webHookDispatcher = webHookDispatcher;
	}

	@PostMapping("/{token}")
	ResponseEntity<Void> webhook(@RequestBody Update update, @PathVariable("token") String token) {

		if (!API_TOKEN.equals(token)) {
			log.warn("Unauthorized webhook request dropped! Token was {}", token);
			return ResponseEntity.notFound().build();
		}

		if (log.isDebugEnabled()) {
			log.debug(update.toString());
		}

		webHookDispatcher.dispatch(update);

		log.info("Returning ok to Telegram");
		return ResponseEntity.ok().build();
	}

}
