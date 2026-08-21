# OpenWeather credentials

v1 is a native Android application. The key is configured and stored **locally on the device at runtime**.

See `decisions/0018-runtime-api-key-storage.md`.

## Strategy

Settings → OpenWeather key:

- enter (password field)
- save
- replace (save again)
- remove
- status: `Not configured` or `Configured · ••••` plus last four characters

The full key is not shown after save.

`ApiKeyStore.read()` returns the plaintext for OpenWeather requests. Phase 1 sends it only as the `appid` query parameter to `api.openweathermap.org`. It is never written into source, Gradle, `BuildConfig`, or logs.

Installing a Phase 1 APK over Phase 0 keeps the same `applicationId` and prefs file (`weather_secrets`), so a saved key survives the update.

## Honesty

- This prevents bundling/publishing the user's key in the APK or Git.
- A key stored in app-private `SharedPreferences` on a user-controlled Android device is **not** cryptographically unknowable to the device owner (especially with root / backup tools).
- EncryptedSharedPreferences was skipped as ceremony for a personal GrapheneOS app.
- No backend is involved.

## Where the key must not exist

| Place | Present? |
| --- | --- |
| Git | No |
| Source / resources | No |
| `BuildConfig` / Gradle properties | No |
| `tasks/`, `docs/` examples, README, commit messages | No |
| Logs / error UI | No (masked status only) |

## Rotation

1. Generate a new key in the OpenWeather account.
2. Settings → paste → Save.
3. Disable the old key.
