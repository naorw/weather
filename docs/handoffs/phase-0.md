# Phase 0 handoff (native Android)

Date: 2026-08-21

Accepted: 2026-08-21

Status: **accepted**

## Outcome

Kotlin + Jetpack Compose Weather APK with graphite Today (static Stockholm sample), four-destination shell, app-private API-key storage, debug and release-like APKs. No OpenWeather network integration. No INTERNET permission.

## Identity

`org.radilabs.weather` / Weather / `0.0.0` (versionCode 1)

## Implementation summary

- Single `:app` module, AGP 8.13.2, Kotlin 2.1.10, Compose BOM 2025.08.01, compileSdk 35, minSdk 29
- Today is a static instrument (hero, 3-hour strip, daily rows + range bars, atmosphere)
- Radar and Cities are placeholders
- Settings saves/replaces/removes a masked OpenWeather key in `SharedPreferences` (`MODE_PRIVATE`)
- Release APK is signed with the debug keystore (no production keystore yet)

## Files / components

- `app/` application, tests, launcher icons
- Gradle wrapper
- `decisions/0018-runtime-api-key-storage.md`
- `docs/android-project.md`, `docs/development.md`, `docs/credentials.md`
- Historical PWA Phase 0 handoff moved to `docs/handoffs/pwa-phase-0.md`

## Tests / build

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease
```

Unit tests: `ApiKeyStore` save/replace/remove/read; shell destinations. Pass.

## APK paths

- Debug: `app/build/outputs/apk/debug/app-debug.apk` (~26 MB)
- Release-like: `app/build/outputs/apk/release/app-release.apk` (~20 MB)

APKs are gitignored. No OpenWeather key strings in the APK dump.

## Local persistence proof

`ApiKeyStore` writes `weather_secrets` / `openweather_api_key`. Robolectric tests prove save, replace, remove, and in-process read. Owner confirmed configured state survives restart on Pixel / GrapheneOS.

## API-key behavior

Enter (hidden), Save, Remove. Status `Not configured` or `Configured · ••••xxxx`. Not in Git, BuildConfig, Gradle, or logs.

Honest limit: app-private storage is not a vault against the device owner. No backend.

## Known limitations

- JDK/SDK are not in the repo; documented as user-local paths
- Release APK uses debug signing
- Debug/release APKs are large (Compose, no minify)
- Static Today only

## Deferred work

- Production signing
- Native Phase 1 OpenWeather (needs INTERNET + stored key)
- Radar, cities, cache, location

## Owner validation (Pixel / GrapheneOS, 2026-08-21)

1. APK installs — pass
2. App launches independently — pass
3. Today visual direction accepted — pass
4. Navigation works — pass
5. API key save works — pass
6. Configured state survives restart — pass
7. API key removal works — pass

## Stop

Phase 1 is not authorized by this handoff. Do not create `tasks/phase-1.md`.
