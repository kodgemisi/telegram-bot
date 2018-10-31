package com.kodgemisi.telegramdevbot;

import org.springframework.context.ApplicationEvent;

/**
 * Created on October, 2018
 *
 * @author destan
 */
public class SubscriberDisabledEvent extends ApplicationEvent {

	private Subscriber subscriber;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public SubscriberDisabledEvent(Subscriber source) {
		super(source);
		this.subscriber = source;
	}
}
