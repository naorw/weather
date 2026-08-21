# OpenWeather credentials

v1 is a native Android application. The key is configured and stored **locally on the device at runtime**.

Do not bake a reusable key into the APK. Do not commit keys. Do not build a backend to hide the key for the personal app.

The PWA used `VITE_OPENWEATHER_API_KEY` inlined into JavaScript. That strategy is superseded by `decisions/0017-native-android-platform.md` (see historical `0008`).

## Strategy

Native Phase 0 must prove that a key can be written and read locally without a network call.

Native Phase 1 may use that stored key for OpenWeather HTTPS requests.

Exact Android storage (for example encrypted preferences / DataStore) is chosen in the authorized phase that implements it.

## Where the key must not exist

| Place | Present? |
| --- | --- |
| Git | No |
| Source / resources in the repo | No |
| `tasks/`, `docs/`, README, commit messages | No |
| Logs / error UI | No |

## Rotation

1. Generate a new key in the OpenWeather account.
2. Update the locally stored value on the device.
3. Disable the old key.
