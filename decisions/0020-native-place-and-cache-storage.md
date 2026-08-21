# 0020 — Native place catalog and weather-cache storage

## Status

Accepted

## Context

`0013` stored snapshots in IndexedDB. Native Phase 3 needs on-device persistence that survives process death and reboot, without Room ceremony for a personal app.

## Decision

- Saved cities and the active `Place` live in app-private SharedPreferences `weather_places` (`MODE_PRIVATE`), separate from the API-key file `weather_secrets`.
- Normalized weather snapshots live as one JSON file per `cacheKey` under `filesDir/weather-cache/`, Gson-encoded `CachedWeather` with `schemaVersion: 1` and `provider: "openweather"`.
- File names replace `:` with `_`.

Failed fetches do not write. Incompatible schema/provider/malformed rows are deleted individually.

## Consequences

API key, cities, and cache are independent files. An overlay install keeps the Phase 0/1 key. Breaking snapshot shape increments `CACHE_SCHEMA_VERSION`.
