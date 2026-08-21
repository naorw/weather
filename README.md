# Weather

A native Android weather instrument for GrapheneOS / Pixel.

Weather presents useful conditions clearly, installs as an APK, and stays useful when connectivity is temporarily unavailable.

Initial data comes from the OpenWeather free APIs. The weather provider must remain replaceable.

Kotlin + Jetpack Compose. The earlier PWA is a completed prototype in Git history, not the v1 platform (`decisions/0017-native-android-platform.md`).

## Product

- Current conditions, hourly forecast, and the multi-day horizon available on the free plan
- Optional device location, city search, and saved cities
- Local caching with explicit stale-state handling
- Runtime locally configured OpenWeather credential
- Native APK, GrapheneOS / Pixel first

v1 is free-first, privacy-conscious, and does not include accounts, analytics, AI commentary, or paid OpenWeather features.

## Design

Dark graphite instrumentation: charcoal surfaces, muted off-white text, restrained cyan/teal and amber accents. The interface should feel like a weather instrument, not a lifestyle dashboard.

Primary screens: **Today**, **Radar**, **Cities**, **Settings**.

## Status

No native implementation phase is authorized.

Native Phase 0 — Android Foundation must be opened explicitly by the owner/planner. Do not create `tasks/phase-0.md` until then.

See `PROJECT.md`, `PHASES.md`, and `docs/development.md`.

## Layout

```
decisions/     architectural and product decisions
docs/          durable technical notes and phase handoffs
icons/         application mark
tasks/         authorized phase tasks (none yet); historical PWA files remain
```
