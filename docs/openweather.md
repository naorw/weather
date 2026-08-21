# OpenWeather integration

The weather-data phase uses only the free OpenWeather 2.5 HTTP APIs over HTTPS.

## Endpoints

Base: `https://api.openweathermap.org`

| Purpose | Path |
| --- | --- |
| Current weather | `/data/2.5/weather` |
| 5-day / 3-hour forecast | `/data/2.5/forecast` |
| Air pollution | `/data/2.5/air_pollution` |
| Direct geocoding | `/geo/1.0/direct` |
| Reverse geocoding | `/geo/1.0/reverse` |

Weather query parameters: `lat`, `lon`, `units=metric`, `appid`. Geocoding uses `q`+`limit` (direct) or `lat`/`lon`+`limit` (reverse), and `appid`. Limit is 5 for search and 1 for reverse.

Not used: One Call 3.0, paid daily/16-day forecast, history, maps.

Captured response examples: `docs/fixtures/openweather/`.

## Free-plan assumptions

- Current weather for a coordinate.
- Forecast is **40 three-hour steps** (about five days). It is not hourly and not 16-day.
- Air pollution current observation is available on the same free key used for weather.
- UV is **not** present on these endpoints. Phase 1 does not invent UV.
- Current `main.temp_min` / `temp_max` are a station envelope, not a true daily range. Daily high/low come from aggregating forecast points.

## Fields relied upon

Current: `coord`, `name`, `sys.country`, `timezone`, `dt`, `main.temp`, `main.feels_like`, optional `temp_min`/`temp_max`/`humidity`/`pressure`, `weather[0].id`/`description`, `wind`, `visibility`, `clouds.all`, optional `rain`/`snow` `1h`.

Forecast: `city` (`name`, `country`, `timezone`, `coord`), `list[]` with `dt`, `main`, `weather`, `wind`, `pop`, optional `rain`/`snow` `3h`.

Air: `list[0].dt`, `list[0].main.aqi` (1–5 OpenWeather scale), `list[0].components.*`.

## Rate limits

Free keys are subject to OpenWeather’s published call budget (commonly 60 calls/minute on the free tier; confirm on the account). One explicit snapshot issues **three** requests (current + forecast + air). No polling.

HTTP 429 becomes application error `rate_limit`.

## AQI meaning

`openWeatherAqi` is OpenWeather’s own 1–5 index (Good → Very Poor). It is not US EPA AQI or CAQI. The UI must not label it as those standards.
