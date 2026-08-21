# Weather cache

Latest successful **normalized** weather snapshots are stored per location. Raw OpenWeather HTTP bodies are not cached.

PWA IndexedDB stores are historical. Native Android stores one Gson JSON file per `cacheKey` under `filesDir/weather-cache/` (`decisions/0020-native-place-and-cache-storage.md`). The record shape below remains the product contract.

## Snapshot record

- `cacheKey` — same as `Place.cacheKey`
- `schemaVersion` — `1` for the prototype contract
- `provider` — `"openweather"`
- `fetchedAtMs` — time of successful provider return
- `snapshot` — application-owned weather snapshot

## Cache key

Identity is the location `cacheKey` (`lat.toFixed(4):lon.toFixed(4)`). Switching cities must read/write only that key.

## Write / update

- On successful refresh, replace the record for that `cacheKey` (one row per location).
- Failed refresh does not write and does not delete a valid row.
- Unusable snapshots (non-finite temperature) are not stored.

## Invalidation / version

Incompatible version, wrong provider string, or malformed snapshot: drop **that row only**. There is no bulk wipe of unrelated places or snapshots.

Breaking snapshot-model changes increment schema version and treat old rows as missing.
