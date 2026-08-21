# Normalized weather models

Application code consumes types in `src/weather/models.ts`. OpenWeather payloads stay inside `src/weather/openweather/`.

## Canonical units

See `decisions/0007-canonical-internal-units.md`.

| Quantity | Unit |
| --- | --- |
| Temperature | °C |
| Wind speed | m/s |
| Pressure | hPa |
| Precipitation | mm |
| Visibility | m |
| Probability / humidity / cloud cover | 0–100 |
| Timestamps | milliseconds since Unix epoch (UTC) |
| Timezone | seconds east of UTC (`timezoneOffsetSeconds`) |

The free 2.5 APIs do not supply an IANA timezone name.

## Optional fields

`undefined` means the provider omitted the value. `0` is a measured zero (for example `pop: 0` or `rain.3h: 0`). Do not collapse those.

## Condition vocabulary

`ConditionCategory`: clear, partly-cloudy, cloudy, overcast, drizzle, light-rain, rain, heavy-rain, thunderstorm, light-snow, snow, fog, unknown.

Unknown OpenWeather ids map to `unknown`. UI must use this vocabulary, not numeric weather codes.

## Major models

- `Location` — display name, coordinates, optional country, timezone offset
- `CurrentConditions` — observation, temperatures, condition, wind, precipitation, atmosphere
- `ForecastPoint` — one 3-hour step
- `DailySummary` — local calendar date, high/low, representative condition, precip summary, `partial` flag
- `AirQuality` — OpenWeather 1–5 index plus µg/m³-style components as supplied
- `WeatherSnapshot` — one explicit fetch of the above

## Provider boundary

`WeatherProvider` in `src/weather/provider.ts`:

- `getCurrent`
- `getForecast`
- `getAirQuality`
- `getSnapshot` — one current + one forecast + one air request; air failure (except auth) leaves `airQuality` unset
