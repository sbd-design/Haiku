# Haiku

Every notification on your phone, turned into a haiku.

A single poem on your lock screen. Nothing else.

---

## How it works

1. Haiku listens to all incoming notifications (via Android's `NotificationListenerService`)
2. Each notification is sent to Claude (`claude-haiku-4-5`) with a prompt to distil it into a 5-7-5 haiku
3. A single, always-on notification on your lock screen is updated with the latest poem
4. The app stores a history of every haiku generated

## Requirements

- Android 8.0+ (API 26)
- A Claude API key from [console.anthropic.com](https://console.anthropic.com)

## Setup

1. Build and install the APK
2. Open the app — follow the onboarding to grant **Notification Access** in Android Settings
3. Enter your Claude API key and tap **Test & Save Key**
4. Tap **Begin** — the lock screen haiku will appear with the next notification

## Project structure

```
app/src/main/java/com/haiku/app/
├── api/              Claude API client + haiku generation
├── data/             Room database + DataStore settings
├── di/               Hilt dependency injection
├── receiver/         Boot receiver
├── service/          NotificationListenerService + lock screen manager
└── ui/               Jetpack Compose screens + ViewModel
```

## Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **AI**: Claude API (`claude-haiku-4-5-20251001`)
- **DI**: Hilt
- **DB**: Room
- **Settings**: DataStore
- **HTTP**: Retrofit + OkHttp

## iOS

iOS does not allow apps to intercept other apps' notifications programmatically.
An iOS version would require a different architecture (e.g. notification forwarding via a server,
or a share-sheet flow). Android-first for now.
