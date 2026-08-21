# Weather

A native Android weather instrument for GrapheneOS / Pixel.

Weather presents useful conditions clearly, installs as an APK, and stays useful when connectivity is temporarily unavailable.

Initial data comes from the OpenWeather free APIs. The weather provider must remain replaceable.

Kotlin + Jetpack Compose. The earlier PWA is a completed prototype in Git history, not the v1 platform (`decisions/0017-native-android-platform.md`).

## Product

- Current conditions, 3-hour forecast steps, and the multi-day horizon available on the free plan
- Optional device location, city search, and saved cities
- Local caching with explicit stale-state handling
- Map instrument with OpenWeather precipitation / cloud-cover map overlays (not observed radar)
- Runtime locally configured OpenWeather credential
- Native APK, GrapheneOS / Pixel first

v1 is free-first, privacy-conscious, and does not include accounts, analytics, AI commentary, or paid OpenWeather features.

## Design

Dark graphite instrumentation: charcoal surfaces, muted off-white text, restrained cyan/teal and amber accents. The interface should feel like a weather instrument, not a lifestyle dashboard.

Primary screens: **Today**, **Radar**, **Cities**, **Settings**.

## Status

Native **Phase 5 — Daily-Use Polish** is **accepted** (2026-08-21). First public release: **Weather v0.1.0**. No later phase is authorized.

See `PROJECT.md`, `PHASES.md`, `docs/handoffs/phase-5.md`, `docs/signing.md`, and `docs/development.md`.

## Layout

```
app/           Kotlin + Compose application
decisions/     architectural and product decisions
docs/          durable technical notes and phase handoffs
icons/         application mark
tasks/         authorized phase tasks; historical PWA under tasks/pwa/
```
