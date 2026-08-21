# Phase 1 handoff (native Android)

Date: 2026-08-21

Accepted: 2026-08-21

Status: **accepted**

Historical PWA Phase 1 handoff is `docs/handoffs/pwa-phase-1.md`.

Do not begin Phase 2. Do not create `tasks/phase-2.md`.

## Outcome

Native Weather reads the Phase 0 runtime OpenWeather key, fetches free current / 5-day 3-hour / air-pollution data for a fixed Stockholm coordinate, maps it into application-owned models, and shows it on the existing Today screen with loading, missing-key, auth, and refresh.

## Implementation summary

- `OpenWeatherProvider` (OkHttp) behind `WeatherProvider`
- Canonical units (`0007`), condition vocabulary, daily aggregation (`0010`)
- Errors: missing key, auth, rate limit, not found, timeout, network, malformed, provider
- Today: live Stockholm; REFRESH; status line. No redesign.
- No weather persistence. Credential prefs file unchanged so overlay install keeps the key.
- `INTERNET` only. versionCode 2 / versionName `0.1.0`

## Endpoints

Base `https://api.openweathermap.org`

- `/data/2.5/weather`
- `/data/2.5/forecast`
- `/data/2.5/air_pollution`

Query: `lat`, `lon`, `units=metric`, `appid`. No One Call. No geocoding.

## Models

`Location`, `CurrentConditions`, `ForecastPoint` (3-hour, not hourly), `DailySummary`, `AirQuality` (OpenWeather 1–5), `WeatherSnapshot`.

UI uses `TodaySnapshot` / glyphs only; OpenWeather ids do not leak into Compose.

## Transformations

- DTO → models in `openweather/Normalize.kt`
- Condition ids → `ConditionCategory`
- Local dates from location `timezoneOffsetSeconds`, not the phone zone
- Daily high/low extrema of 3-hour points; representative condition by severity; precip max probability / summed mm; `partial` if fewer than 8 points

## Errors

See `docs/weather-errors.md`. Compose catches `WeatherError` only.

## Tests / build

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease
```

Unit tests passed (fixtures + MockWebServer). No live OpenWeather in CI.

## APK paths

- Debug: `app/build/outputs/apk/debug/app-debug.apk` (~28 MB)
- Release-like: `app/build/outputs/apk/release/app-release.apk` (~21 MB)

Gitignored. Manifest: `INTERNET`. Prefs key names exist in dex; no user API key is bundled. `appid=redacted` appears only as the redaction helper.

## Known limitations

- Free 3-hour / ~5-day horizon; partial first/last days
- AQ fetched, not displayed
- UV not available
- Debug-signed release APK

## Deferred work

- Phase 2 UI
- Phase 3 cities / cache / offline
- Production signing

## Owner validation (Pixel / GrapheneOS, 2026-08-21)

1. Install Phase 1 APK over Phase 0 — pass
2. Saved key survives update — pass
3. Live Stockholm weather loads — pass
4. Values look plausible — pass
5. Refresh works — pass
6. Remove key → missing-key state — pass
7. Re-enter key → live weather — pass
8. Invalid key → distinguishable auth error — pass
9. Navigation and Phase 0 Settings still work — pass

## Stop

Phase 2 is not authorized by this acceptance. Do not create `tasks/phase-2.md`.
