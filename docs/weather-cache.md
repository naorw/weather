# Weather cache

Latest successful **normalized** `WeatherSnapshot` values are stored per location. Raw OpenWeather HTTP bodies are not cached.

## Database

- Name: `org.radilabs.weather` (not the Phase 0 proof DB `org.radilabs.weather.phase0`)
- Version: 1
- Stores: `places` (key `id`), `snapshots` (key `cacheKey`), `kv` (`active`, `order`)

## Snapshot record

```ts
{
  cacheKey: string;       // same as Place.cacheKey
  schemaVersion: 1;
  provider: "openweather";
  fetchedAtMs: number;    // Date.now() at successful provider return
  snapshot: WeatherSnapshot;
}
```

## Cache key

Identity is the location `cacheKey` (`lat.toFixed(4):lon.toFixed(4)`). Switching cities must read/write only that key.

## Write / update

- On successful refresh, replace the record for that `cacheKey` (one row per location).
- Failed refresh does not write and does not delete a valid row.
- Unusable snapshots (non-finite temperature) are not stored.

## Invalidation / version

`schemaVersion` must equal `1`. Incompatible version, wrong provider string, or malformed snapshot: drop **that row only**. There is no bulk wipe of unrelated places or snapshots.

Future breaking model changes increment `CACHE_SCHEMA_VERSION` in `src/cache/schema.ts` and treat old rows as missing.
