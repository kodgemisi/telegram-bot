package com.kodgemisi.telegramdevbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

/**
 * Created on October, 2018
 *
 * @author destan
 */
@Service
@Slf4j
class UserOperationsService {

	private final SubscriberRepository subscriberRepository;

	private final ApplicationEventPublisher applicationEventPublisher;

	UserOperationsService(SubscriberRepository subscriberRepository, ApplicationEventPublisher applicationEventPublisher) {
		this.subscriberRepository = subscriberRepository;
		this.applicationEventPublisher = applicationEventPublisher;
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
			return;
		}

		final Subscriber subscriber = new Subscriber(user);
		subscriberRepository.save(subscriber);
		applicationEventPublisher.publishEvent(new SubscriberRegisteredEvent(subscriber));
	}

	void disableUser(User user) {
		final Subscriber subscriber = subscriberRepository.findByTelegramId(user.getId()).orElseThrow(IllegalStateException::new);
		subscriber.disable();
		subscriberRepository.save(subscriber);
		applicationEventPublisher.publishEvent(new SubscriberDisabledEvent(subscriber));
	}

	boolean isAdmin(Integer telegramId) {
		final Optional<Subscriber> admin = subscriberRepository.findAdminByTelegramId(telegramId);
		return admin.isPresent();
	}
}
