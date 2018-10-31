package com.kodgemisi.telegramdevbot;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SubscriberRegisteredEvent extends ApplicationEvent {

	private Subscriber subscriber;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public SubscriberRegisteredEvent(Subscriber source) {
		super(source);
		this.subscriber = source;
	}
}