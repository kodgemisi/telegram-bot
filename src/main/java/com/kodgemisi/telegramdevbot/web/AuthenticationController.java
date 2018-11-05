package com.kodgemisi.telegramdevbot.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Controller
@RequestMapping("/telegram-login")
@Profile("dev")
class AuthenticationController {

	private final String CALL_BACK_URL;

	AuthenticationController(@Value("${app.base-url}") String baseUrl) {
		CALL_BACK_URL = baseUrl + "/telegram-login";
	}

	@GetMapping("/form")
	String loginForm(Model model, @AuthenticationPrincipal Object o)  {
		model.addAttribute("baseUrl", CALL_BACK_URL);
		return "telegram-login";
	}

}