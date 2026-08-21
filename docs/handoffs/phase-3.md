# Phase 3 handoff (native Android)

Date: 2026-08-21

Accepted: 2026-08-21

Status: **accepted**

Historical PWA Phase 3 handoff is `docs/handoffs/pwa-phase-3.md`.

Do not begin Phase 4. Do not create `tasks/phase-4.md`. Do not implement Radar.

## Outcome

Cities search/save/switch/remove, optional one-shot device location, persistent active place, per-location normalized weather cache, CACHED/STALE on Today, cache-first startup, failed refresh preserves cache, generation-token race safety. Phase 2 Today layout kept. Radar still a placeholder.

## Implementation summary

- `Place` identity `lat:lon` to 4 decimals (HALF_UP, `0012`)
- `PrefsPlaceCatalog` in `weather_places` (not the API-key prefs)
- `FileSnapshotCache` Gson JSON under `filesDir/weather-cache/`
- OpenWeather `/geo/1.0/direct` (limit 5) and `/geo/1.0/reverse`
- `WeatherSession` generation + in-flight key so A cannot paint B
- `DeviceLocator` one-shot `LocationManager`; permission only after Cities action
- Stockholm default/fallback (`0015`)
- versionCode 4 / versionName `0.3.0`

## Persistence model

| Store | File | Contents |
| --- | --- | --- |
| API key | SharedPreferences `weather_secrets` | unchanged from Phase 0 |
| Places | SharedPreferences `weather_places` | saved list + active Place |
| Weather | `filesDir/weather-cache/{lat}_{lon}.json` | last successful snapshot |

## Cache schema

`CachedWeather`: `cacheKey`, `schemaVersion` = 1, `provider` = `openweather`, `fetchedAtMs`, `snapshot` (application-owned). Incompatible rows dropped individually. Failed fetch does not write.

## Staleness rules

`0014`: age < 30 min → CACHED; ≥ 30 min → STALE; successful live fetch for the current place → LIVE (no badge). Age: JUST NOW / N MIN AGO / N H AGO / N D AGO.

## Permission behavior

Never requested on startup. Cities “Use device location” requests FINE+COARSE (required for targetSdk 35). Denial: copy only, search still works, no nag loop. No `ACCESS_BACKGROUND_LOCATION`.

## Search behavior

Explicit GO. Up to five hits with name, region/state, country. Select saves (deduped) and activates. Empty → “No results.”

## Recovery behavior

Explicit Refresh, returning to Today, and `ConnectivityManager` default-network callback. No polling, no WorkManager.

## Race-safety mechanism

Monotonic generation plus active `cacheKey`. Completions for a stale generation or a different key are discarded. Same-key in-flight refresh is reused.

## Tests / build

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease
```

Pass (identity, catalog persist, geo mapping, cache isolation/schema, staleness, A→B race, failed refresh keeps cache, cache-first then live). No live GPS/OpenWeather.

## APK paths

- Debug: `app/build/outputs/apk/debug/app-debug.apk` (~29 MB)
- Release-like: `app/build/outputs/apk/release/app-release.apk` (~21 MB)

Permissions: INTERNET, ACCESS_NETWORK_STATE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION. No background location.

## Known limitations

- GrapheneOS may disable location globally; denial path is required
- Coarse-only grant yields a less precise fix, then rounded to 4 decimals
- Gson snapshot encoding is not a migration-friendly protobuf
- Device location is not auto-saved (by design)

## Deferred work

- Phase 4 Radar/maps
- Production signing

## Owner validation (Pixel / GrapheneOS, 2026-08-21)

1. City search/save/switch — pass
2. Active city persistence — pass
3. Saved locations persistence — pass
4. Device location — pass
5. Permission denial path — pass
6. Airplane-mode relaunch with cached weather — pass
7. Cached/stale indication — pass
8. Failed refresh preserving cache — pass
9. Reconnect recovery to LIVE — pass
10. Rapid city switching without wrong-location repaint — pass
11. API key persistence — pass
12. Phase 2 Today design remaining intact — pass

## Stop

Phase 4 is not authorized by this acceptance. Do not create `tasks/phase-4.md`.
