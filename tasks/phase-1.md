# Native Phase 1 — Weather Data

## Status

**Accepted** 2026-08-21 on Pixel / GrapheneOS.

Owner authorized Native Phase 1 on 2026-08-21. Native Phase 0 remains accepted.

Do not begin Native Phase 2. Do not create `tasks/phase-2.md`.

---

## Objective

Connect Weather to the OpenWeather free APIs and establish trustworthy application-owned weather models.

**data acquisition → normalization → deterministic transformation → stable errors**

Fixed validation location: Stockholm `59.3293, 18.0686`.

---

## Files Changed

- `TASKS.md`, `tasks/README.md`, `tasks/phase-1.md`
- `app/` weather provider, Today wiring, INTERNET permission, version `0.1.0` / versionCode 2
- `docs/weather-models.md`, `docs/openweather.md`, `docs/weather-errors.md`, `docs/android-project.md`, `docs/credentials.md`, `docs/development.md`
- `docs/handoffs/phase-1.md`
- `decisions/0019-native-provider-boundary.md`

---

## Tests Performed

`./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease`

Unit tests (no live OpenWeather): conditions, daily aggregation, fixture normalize, MockWebServer snapshot/auth/missing-key/air-failure/timeout, error mapping, `ApiKeyStore`, destinations.

---

## Results

Pass. Debug and release-like APKs produced. Manifest has `INTERNET`. No location permission. No bundled API key.

Owner validation on Pixel / GrapheneOS (2026-08-21): overlay install, key survival, live Stockholm, plausible values, refresh, missing-key, re-enter key, invalid-key auth error, and Phase 0 navigation/Settings — all pass.

---

## Known Limitations

- Today layout is still the Phase 0 instrument with live data + status/refresh, not Phase 2 design
- Air quality is fetched and modeled, not shown on Today
- Free forecast is ~five days of 3-hour points; days are often `partial`
- Release APK still debug-signed
- No weather cache

---

## Deferred Work

* Phase 2 Weather Instrument UI
* Phase 3 locations / cache / offline
* Production signing
* Geocoding APIs

---

## Decisions Created

* `0019-native-provider-boundary.md`

---

# Completion Rule

Stop at handoff. Do not begin Phase 2. Do not create `tasks/phase-2.md`.
