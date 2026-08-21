# Phase 1 handoff

Date: 2026-08-21

Accepted: 2026-08-21

## Outcome

OpenWeather free 2.5 current, 3-hour forecast, and air-pollution calls sit behind `WeatherProvider`. Responses become application-owned models. Daily summaries are deterministic. Errors are `WeatherError` codes. Today remains the Phase 0 static screen.

## Owner acceptance

2026-08-21: live Stockholm probe succeeded in Firefox.

- 18.11°C, overcast clouds
- 40 forecast points
- 6 local days (first and last partial)
- Air quality fair (OpenWeather index 2)

## Proof

Settings → Fetch Stockholm weather (requires `VITE_OPENWEATHER_API_KEY`).

`npm test` includes an optional live file when the key is set.

## Docs

* `docs/openweather.md`
* `docs/weather-models.md`
* `docs/forecast-aggregation.md`
* `docs/credentials.md`
* `docs/development.md`

## Decisions

0007 units, 0008 credentials, 0009 provider boundary, 0010 daily aggregation.

## Stop

Phase 2 is not authorized by this handoff.
