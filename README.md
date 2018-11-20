# Telegram Bot

This is a Telegram bot which works like a _restricted mail group_ where only admins can send messages directly. When a regular user sends a message (via `/suggest <link>`) it's moderated by admins. If a suggestion by a regular user is accepted then it becomes a message otherwise it's ignored and only be seen by admins. Just like a mail group, all messages are delivered to all users.

Purpose of this bot is to create a medium where people can receive a curated stream of technological updates.

# Usage

| Command           	| Description                                                                            	|
|-------------------	|----------------------------------------------------------------------------------------	|
| `/start`          	| Subscribe/Resubscribe to updates                                                       	|
| `/stop`           	| Unsubscribe from updates                                                               	|
| `/suggest <link>` 	| Suggest sharing a link with all other users. If one of the admins accepts it's shared. 	|
| `/send <link>`    	| Only available to admins. Directly shares a link with all other users and admins.      	|

# Deployment

You can fork and create your own bot. Only thing you need to do is to create a bot via [BotFather](https://core.telegram.org/bots#6-botfather) and then use its token.

Following environment variables are required:

* `BASE_URL` - **without a trailing slash**
* `TOKEN` - 
* `MONGO_URI`

To test in local environment you need to use [ngrok](https://ngrok.com) which is an application that exposes your local ip to a globally reachable temporary domain name so that you can receive Telegram's webhook request while running on `localhost`.

# Development Resources

* [Bot Samples](https://core.telegram.org/bots/samples)
* [Update Pojo](https://github.com/leocus/telegramBotUtilities/blob/master/src/org/altervista/leocus/telegrambotutilities/Update.java)
* [Update Pojo](https://github.com/rubenlagus/TelegramBots/blob/master/telegrambots-meta/src/main/java/org/telegram/telegrambots/meta/api/objects/Update.java)

# LICENSE

© Copyright 2018 Kod Gemisi Ltd.

Mozilla Public License 2.0 (MPL-2.0)

https://tldrlegal.com/license/mozilla-public-license-2.0-(mpl-2)

MPL is a copyleft license that is easy to comply with. You must make the source code for any of your changes available under MPL, 
but you can combine the MPL software with proprietary code, as long as you keep the MPL code in separate files. 
Version 2.0 is, by default, compatible with LGPL and GPL version 2 or greater. You can distribute binaries under a proprietary license, 
as long as you make the source available under MPL.

[See Full License Here](https://www.mozilla.org/en-US/MPL/2.0/)
