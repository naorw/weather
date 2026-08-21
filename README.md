# Weather

A small, installable weather PWA for Android / GrapheneOS.

Weather is a fast, visually distinctive weather instrument. It presents useful conditions clearly, works well as an installed Android PWA, and stays useful when connectivity is temporarily unavailable.

Initial data comes from the OpenWeather free APIs. The weather provider must remain replaceable.

## Product

- Current conditions, hourly forecast, and the multi-day horizon available on the free plan
- Optional device location, city search, and saved cities
- Local caching with explicit stale-state handling
- Installable PWA (standalone), mobile / portrait first

v1 is free-first, privacy-conscious, and does not include accounts, analytics, AI commentary, or paid OpenWeather features.

## Design

Dark graphite instrumentation: charcoal surfaces, muted off-white text, restrained cyan/teal and amber accents. The interface should feel like a weather instrument, not a lifestyle dashboard.

Primary screens: **Today**, **Radar**, **Cities**, **Settings**.

## Status

Phase 0, Phase 1, and Phase 2 are **accepted** (2026-08-21). Phase 3 is not authorized yet.

See `PROJECT.md` for product intent, `PHASES.md` for phase contracts, and `docs/development.md` for commands.

## Quick start

```sh
npm install
npm run dev
```

Production / PWA check:

```sh
npm test
npm run build
npm run preview
```

## Layout

```
decisions/     architectural and product decisions
docs/          durable technical notes and phase handoffs
icons/         application icons
src/           application source
tasks/         currently authorized phase task files
tests/         tests
```
