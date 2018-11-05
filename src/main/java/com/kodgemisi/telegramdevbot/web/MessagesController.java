package com.kodgemisi.telegramdevbot.web;

import com.kodgemisi.telegramdevbot.message.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Controller
@RequestMapping("/messages")
class MessagesController {

	private final MessageService messageService;

	public MessagesController(MessageService messageService) {
		this.messageService = messageService;
	}

	@GetMapping
	@ResponseBody
	String list() {
		return "list";
	}

	@PostMapping
	ResponseEntity<Void> sendMessage(@RequestParam("id") String id, @RequestParam("text") String text) {

		messageService.sendMessage(text, Integer.valueOf(id));

		return ResponseEntity.ok().build();
	}

}
