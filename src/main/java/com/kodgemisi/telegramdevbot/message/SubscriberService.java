package com.kodgemisi.telegramdevbot.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Service
@Slf4j
class SubscriberService {

	private final SubscriberRepository subscriberRepository;

	private final NotificationService notificationService;

	SubscriberService(SubscriberRepository subscriberRepository, NotificationService notificationService) {
		this.subscriberRepository = subscriberRepository;
		this.notificationService = notificationService;
	}

	void registerOrActivateUser(User user) {
		if (user.getBot()) {
			log.info("User {} is a bot, skipping registration...", user);
			return;
		}

		if (subscriberRepository.findByTelegramId(user.getId()).isPresent()) {
			log.info("User {} already registered, activating if disabled", user);
			final Subscriber existingSubscriber = subscriberRepository.findByTelegramId(user.getId()).get();
			existingSubscriber.activate();
			subscriberRepository.save(existingSubscriber);
			notificationService.sendReactivatedMessage(existingSubscriber);
			return;
		}

		final Subscriber subscriber = new Subscriber(user);
		subscriberRepository.save(subscriber);
		notificationService.sendActivatedMessage(subscriber);
	}

	void disableUser(User user) {
		final Subscriber subscriber = subscriberRepository.findByTelegramId(user.getId()).orElseThrow(IllegalStateException::new);
		subscriber.disable();
		subscriberRepository.save(subscriber);
		notificationService.sendDisabledMessage(subscriber);
	}
}
