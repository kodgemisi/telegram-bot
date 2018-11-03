package com.kodgemisi.telegramdevbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@Component
@Slf4j
class WebHookRegistrator {

	private final RestTemplate restTemplate;

	private final String SET_WEBHOOK_URL;

	private final MultiValueMap<String, String> map;

	WebHookRegistrator(RestTemplate restTemplate, @Value("${app.url.set-webhook}") String setWebhookUrl,
			@Value("${app.url.webhook-callback}") String callbackUrl) {
		this.restTemplate = restTemplate;
		this.SET_WEBHOOK_URL = setWebhookUrl;

		map = new LinkedMultiValueMap<>();
		map.add("url", callbackUrl);
	}

	@PostConstruct
	private void init() {
		this.setWebHook();
	}

	private void setWebHook() {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		final ResponseEntity<String> response = restTemplate.postForEntity(SET_WEBHOOK_URL, request, String.class);

		if(!response.getStatusCode().is2xxSuccessful()) {
			log.error("Cannot set web hook. Response: {}", response);
			throw new IllegalStateException("Cannot set web hook");
		}

		log.info("Web hook is set to {}", map.get("url"));
	}

}
