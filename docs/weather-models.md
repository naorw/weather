# Normalized weather models

Application code consumes application-owned models. OpenWeather payloads stay inside the provider client.

The Kotlin types live in `org.radilabs.weather.weather`. OpenWeather payloads stay inside `org.radilabs.weather.weather.openweather` (`decisions/0019-native-provider-boundary.md`).

The PWA types lived in `src/weather/models.ts`; native Kotlin types match this contract unless a later decision changes it.

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

Omitted means the provider did not supply the value. `0` is a measured zero (for example probability 0 or rain 0 mm). Do not collapse those.

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

`WeatherProvider` shape:

- `getSnapshot` — one current + one forecast + one air request; air failure (except auth / missing key) leaves `airQuality` unset

Geocoding (`searchPlaces`, `reverseGeocode`) belongs to the locations phase, not Phase 1.

Phase 1 uses a single fixed coordinate: Stockholm `59.3293, 18.0686` (`STOCKHOLM`).
