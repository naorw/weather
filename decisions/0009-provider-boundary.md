# 0009 — Weather provider boundary

## Status

Accepted

## Context

OpenWeather must be replaceable without rewriting UI, without a plugin framework.

## Decision

UI and screens import only `src/weather` public types and `WeatherProvider`. OpenWeather HTTP, URLs, and payloads live under `src/weather/openweather/`. There is a single concrete provider created by `createWeatherProvider`.

## Consequences

Do not pass raw OpenWeather objects into screens. Adding another provider later means a new client behind the same `WeatherProvider` shape, not a registry.
