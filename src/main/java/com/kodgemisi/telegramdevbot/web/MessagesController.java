package com.kodgemisi.telegramdevbot.web;

import com.kodgemisi.telegramdevbot.message.MessageService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Controller
@RequestMapping("/messages")
@Profile("dev")
class MessagesController {

	private final MessageService messageService;

	public MessagesController(MessageService messageService) {
		this.messageService = messageService;
	}

	@PostMapping
	ResponseEntity<Void> sendMessage(@RequestParam("id") String id, @RequestParam("text") String text) {

		//FIXME require user authentication for this end point

		messageService.sendMessage(text, Integer.valueOf(id));

		return ResponseEntity.ok().build();
	}

}
