## Registering Commands

```
start - Register for updates
suggest - Suggest an entry, if it's accepted it'll be sent to everyone
stop - Disable updates until you give /start command again
```

https://core.telegram.org/bots#commands

## Registering Webhook

`curl -F "url= https://<domain_from_ngrok>/webhook/<token>" https://api.telegram.org/bot<token>/setWebhook`