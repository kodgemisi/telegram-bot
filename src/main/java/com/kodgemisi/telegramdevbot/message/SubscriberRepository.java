package com.kodgemisi.telegramdevbot.message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface SubscriberRepository extends MongoRepository<Subscriber, String> {

	Optional<Subscriber> findByUserName(String userName);

	Optional<Subscriber> findByTelegramId(Integer id);

	/**
	 * CAUTION! returned {@link Subscriber}s' active fields are false but in reality they are true!
	 *
	 * @return
	 */
	@Query(value = "{active: true}", fields = "{telegramId : 1}")
	Set<Subscriber> findAllActiveUserIds();

	@Query(value = "{admin: true, telegramId: ?0}", fields = "{telegramId : 1}")
	Optional<Subscriber> findAdminByTelegramId(Integer id);

	@Query(value = "{admin: true}") // , fields = "{telegramId : 1}"
	Set<Subscriber> findAllAdmins();

	@Query(value = "{admin: null, active: true}")
	Set<Subscriber> findAllActiveRegularUsers();
}