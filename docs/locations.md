# Locations

Application-owned places live in `src/locations/`. OpenWeather geocoding payloads never leave `src/weather/openweather/`.

## Model

`Place`:

- `id` — stable local identity, equal to `cacheKey`
- `cacheKey` — weather-cache lookup key
- `displayName`
- `coordinates` (`latitude`, `longitude`)
- optional `country`, `region`
- `source`: `saved` | `search` | `device` | `default`

Search hits are `PlaceCandidate` on the provider boundary, then converted with `placeFromFields`.

## Identity / deduplication

`cacheKey` is `lat.toFixed(4):lon.toFixed(4)` (~11 m). Repeated search of the same place reuses that key. Saved-city `put` uses `id = cacheKey`, so a second save cannot create a second row.

Device location is **not** auto-saved. GPS jitter therefore does not multiply the saved list. The user can save the active device place from Cities.

## Active location

The full `Place` object is stored under IndexedDB `kv` key `active` in database `org.radilabs.weather`. Reload/PWA relaunch restores it even if it is not in the saved list (device location).

Removing the active saved city falls back to the first remaining saved city, then to the first-run default.

Corrupt saved rows are dropped individually. A missing/corrupt active record is replaced with the first-run default.

## First-run fallback

If nothing is stored, the active place is Stockholm SE (`59.3293, 18.0686`, source `default`). The app does not request geolocation on startup. The user can search or optionally use device location from Cities.

## Search

Cities search is explicit (form submit). OpenWeather Geocoding 1.0 `direct` returns up to five hits with name, state/region, and country so similarly named places can be distinguished. Selecting a result saves it (deduped) and makes it active.
