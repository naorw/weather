# 0019 — Native OpenWeather provider boundary

## Status

Accepted

## Context

`0009` defined the provider boundary for the abandoned PWA (`src/weather`). Native Phase 1 needs the same rule in Kotlin.

## Decision

Compose UI imports application-owned types from `org.radilabs.weather.weather` and `WeatherProvider`. OpenWeather HTTP, URLs, JSON DTOs, and mapping live in `org.radilabs.weather.weather.openweather`.

There is a single concrete provider: `OpenWeatherProvider`. Do not add a plugin registry.

`units=metric` is requested from OpenWeather and copied into canonical units (`0007`) inside the adapter.

## Consequences

Screens must not import OpenWeather JSON types or numeric weather codes. A second provider later is a new class behind `WeatherProvider`, not a marketplace.
