# 0018 — Runtime OpenWeather key storage

## Status

Accepted

## Context

Native Phase 0 must store an OpenWeather key on-device at runtime. The key must not be baked into the APK. EncryptedSharedPreferences adds Keystore ceremony for a personal GrapheneOS app whose user already controls the device.

## Decision

Store the key in an app-private `SharedPreferences` file (`weather_secrets` / `openweather_api_key`, `MODE_PRIVATE`).

Settings can save, replace, and remove it. The full key is not shown again after save (masked label only). The value is readable in-process for Phase 1.

Do not put the key in `BuildConfig`, Gradle properties, resources, logs, or Git.

## Consequences

This prevents publishing the user's key inside the APK. It is not cryptographically unknowable to a device owner with filesystem access. There is no backend. Phase 1 must read through `ApiKeyStore`, not a new secret channel.
