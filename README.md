# Wx Weather

**A native Android weather app with zero tracking.**

Current conditions, forecasts, saved cities, offline-friendly caching, and a map instrument — without accounts, analytics, or the usual surveillance garnish.

**[Product page & screenshots → radilabs.com/wx-weather](https://www.radilabs.com/wx-weather/)**

## What it does

- Current conditions and 3-hour forecast steps
- Multi-day forecast horizon
- Optional device location
- City search and saved cities
- Local caching with explicit stale-state handling
- Map instrument with precipitation and cloud-cover overlays
- Native Android APK
- GrapheneOS / Pixel first

The map overlays come from OpenWeather and are **not observed radar**.

## Privacy

Wx Weather is deliberately boring about your data:

- No accounts
- No analytics
- No tracking
- No AI commentary
- Location is optional
- OpenWeather credentials are configured locally at runtime

## Design

Dark graphite instrumentation: charcoal surfaces, muted off-white text, restrained cyan/teal and amber accents.

The interface is meant to feel like a **weather instrument**, not a lifestyle dashboard.

Primary screens: **Today · Map · Cities · Settings**

## Weather data

The current implementation uses the OpenWeather free APIs for forecast data and map overlays.

The provider boundary is intentionally replaceable; OpenWeather is an implementation choice, not the product architecture.

## Tech

- Kotlin
- Jetpack Compose
- Native Android
- Local caching
- Replaceable weather-provider boundary

The earlier PWA is a completed prototype preserved in Git history. Native Android is the current platform; see [ADR 0017](decisions/0017-native-android-platform.md).

## Build

Clone the repository and build with the included Gradle wrapper:

```bash
git clone https://github.com/naorw/weather.git
cd weather
./gradlew assembleDebug
```

Development and signing details live in [docs/development.md](docs/development.md) and [docs/signing.md](docs/signing.md).

## Status

**Weather v0.1.0** is the first public release.

The native daily-use baseline is complete. Work after v0.1.0 continues as small, explicitly scoped improvements rather than a platform rewrite.

For the detailed project record, see [PROJECT.md](PROJECT.md), [PHASES.md](PHASES.md), and [TASKS.md](TASKS.md).

## Repository layout

```text
app/           Kotlin + Compose application
decisions/     architecture and product decisions
docs/          durable technical notes and handoffs
icons/         application mark
tasks/         authorized and historical implementation tasks
```

## Built with Radi

Wx Weather was built with **Radi** participating in research, product decisions, and implementation.

**[See Wx Weather at Radilabs →](https://www.radilabs.com/wx-weather/)**

## License

[MIT](LICENSE)
