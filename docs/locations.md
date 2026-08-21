# Locations

Application-owned places are distinct from OpenWeather geocoding payloads. Provider JSON must not leak into UI.

## Model

`Place` (conceptual):

- `id` — stable local identity, equal to `cacheKey`
- `cacheKey` — weather-cache lookup key
- `displayName`
- `coordinates` (`latitude`, `longitude`)
- optional `country`, `region`
- `source`: `saved` | `search` | `device` | `default`

Search hits become places after provider normalization.

## Identity / deduplication

`cacheKey` is `lat.toFixed(4):lon.toFixed(4)` (~11 m). Repeated search of the same place reuses that key. A second save must not create a second row.

Device location is **not** auto-saved. GPS jitter therefore does not multiply the saved list. The user can save the active device place from Cities.

## Active location

The full `Place` persists locally and restores on process death / reboot even if it is not in the saved list (device location).

Removing the active saved city falls back to the first remaining saved city, then to the first-run default.

Corrupt saved rows are dropped individually. A missing/corrupt active record is replaced with the first-run default.

PWA IndexedDB (`org.radilabs.weather`) was the prototype store. Native persistence is SharedPreferences `weather_places` plus per-location cache files. See `decisions/0020-native-place-and-cache-storage.md`.

## First-run fallback

If nothing is stored, the active place is Stockholm SE (`59.3293, 18.0686`, source `default`). The app does not request location permission on startup. The user can search or optionally use device location from Cities.

## Search

Cities search is explicit. OpenWeather Geocoding 1.0 `direct` returns up to five hits with name, state/region, and country so similarly named places can be distinguished. Selecting a result saves it (deduped) and makes it active.
