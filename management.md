## Registering Commands

```
start - Register for updates
stop - Disable updates until you give /start command again
```

https://core.telegram.org/bots#commands

## Registering Webhook

`curl -F "url= https://<domain_from_ngrok>/webhook/<token>" https://api.telegram.org/bot<token>/setWebhook`