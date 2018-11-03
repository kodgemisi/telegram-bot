package com.kodgemisi.telegramdevbot.suggestion;

import org.springframework.data.mongodb.repository.MongoRepository;

interface SuggestionRepository extends MongoRepository<Suggestion, String> {

}