package com.kodgemisi.telegramdevbot.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@RequestMapping("/")
@Controller
@Slf4j
class HomeController {

	@GetMapping
	String home() {
		return "index";
	}
}
