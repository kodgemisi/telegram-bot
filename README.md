# Telegram Bot

https://telegram.me/JavaFeedBot

# Development Resources

* [Bot Samples](https://core.telegram.org/bots/samples)
* [Update Pojo](https://github.com/leocus/telegramBotUtilities/blob/master/src/org/altervista/leocus/telegrambotutilities/Update.java)
* [Update Pojo](https://github.com/rubenlagus/TelegramBots/blob/master/telegrambots-meta/src/main/java/org/telegram/telegrambots/meta/api/objects/Update.java)
* [HTTP tunneling](https://stackoverflow.com/a/36734312/878361)
  * `ngrok http 80`

# Samples

### Send Message

`https://api.telegram.org/bot<token>/sendMessage`

```
{
	"chat_id": 255538699,
	"text": "https://twitter.com/sliconf/status/1057277957268496384"
}
```

### Webhook request triggered via /start

```
{"update_id":485850693,
"message":{"message_id":15,"from":{"id":xxx,"is_bot":false,"first_name":"Ersan","last_name":"Ceylan","language_code":"en-TR"},"chat":{"id":636547178,"first_name":"Ersan","last_name":"Ceylan","type":"private"},"date":1540987394,"text":"/start","entities":[{"offset":0,"length":6,"type":"bot_command"}]}}
```

