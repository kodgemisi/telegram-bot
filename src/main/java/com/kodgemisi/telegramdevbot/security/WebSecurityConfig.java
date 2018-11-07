package com.kodgemisi.telegramdevbot.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created on November, 2018
 *
 * @author destan
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final String telegramToken;

	WebSecurityConfig(@Value("${app.telegram-api-token}") String telegramToken) {
		this.telegramToken = telegramToken;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(new TelegramAuthenticationProvider(telegramToken));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http
			.requestMatchers()
				.mvcMatchers("/messages", "/suggestions")
				.and()

		.authorizeRequests()
			.anyRequest().hasAuthority("ADMIN")
			.and()

		.logout()
			.and()

		.addFilterAfter(telegramAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		//.authenticationProvider(new TelegramAuthenticationProvider(telegramToken)); //FIXME doesn't work for some reason. Ask it.
		//@formatter:on
	}

	@Bean
	AbstractAuthenticationProcessingFilter telegramAuthenticationFilter() throws Exception {
		return new TelegramAuthenticationFilter(authenticationManager());
	}

}

@Configuration
class WebSecurityConfigPermitedUrls extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http
			.csrf().ignoringAntMatchers("/webhook/**")
				.and()
			.authorizeRequests()
				.anyRequest()
				.permitAll();
		//@formatter:on
	}
}
