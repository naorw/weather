# Phase 5 handoff (native Android)

Date: 2026-08-21

Status: **implemented / awaiting owner acceptance**

Do not tag `v0.1.0`. Do not create the GitHub Release until the owner accepts and confirms the production-signed install path.

## Release candidate version

* `versionName` **0.1.0**
* `versionCode` **6**
* Settings shows runtime `PackageInfo` (`Weather 0.1.0 (6) · org.radilabs.weather`)

## Audit findings addressed

Blockers: B-1 version/dynamic Settings; B-2 MapLibre `onCreate` before observer; B-3 cache write never crashes (session + file write).

High: H-1 cache I/O on `Dispatchers.IO`; H-2 coroutine containment with cancellation rethrow; H-3 `toWeatherError()`; H-4 adaptive launcher + density buckets; H-5 production signing **path** (no debug-key fallback).

Medium taken: atomic cache write; cancellable location; exhaustive Today `when`; typed `Freshness`; dead `PlaceholderScreen` / `weatherQuery` / `usableSnapshot`; Settings copy; TalkBack labels; skip automatic refresh when cache is still CACHED; MapView `onLowMemory`.

## Findings deferred

* **H-6 R8/minify:** evaluated and **left off**. APK size is dominated by MapLibre native `.so` files. Enabling R8 without device confirmation of MapLibre + Gson is an unnecessary v0.1.0 risk. Keep rules exist in `app/proguard-rules.pro` for later.
* **Production-signed APK / SHA256:** signing config is required, but **no production keystore exists on this machine** and none was generated. Owner must follow `docs/signing.md`, then `./gradlew :app:prepareReleaseArtifact`.
* Extra Maps 1.0 layers, observed radar, broader code audit, ABI splits.

## Lifecycle

Radar: `runMapAttach { onCreate }` then register lifecycle observer. `onLowMemory` / trim forwarded to `MapView`.

## Persistence / cache

Atomic temp+rename writes. Write failures are swallowed; in-memory LIVE snapshot remains. Corrupt rows still dropped on read. Cache read/write from refresh coroutines on IO.

## Accessibility

See `docs/accessibility.md`. Practical TalkBack/touch-target/contrast pass. Not WCAG certified.

## Network / performance

Startup still does one live fetch. Returning to Today or connectivity `onAvailable` skips a new fetch when the on-disk snapshot is still **CACHED** (&lt; 30 min). Manual Refresh always fetches. In-flight reuse unchanged. No polling / WorkManager.

## Release signing state

**Path established. Artifact not built.** `assembleRelease` fails until `weather.release.*` is set in gitignored `local.properties` (or env vars). See `docs/signing.md`.

First production install **cannot** overlay Phase 0–4 debug-signed apps. Uninstall wipes key/cities/cache.

## APK path

* Debug (this session): `app/build/outputs/apk/debug/app-debug.apk` — version 0.1.0 / 6, debug-signed. For development only.
* Production: `dist/weather-v0.1.0.apk` after owner configures the keystore and runs `:app:prepareReleaseArtifact`.

## SHA256

Not generated. Requires the production APK.

## Tests / results

```sh
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

**PASS.** Includes cache write failure, atomic write, skip-automatic-refresh, MapView attach order, `toWeatherError`, AppVersion label, prior Phase 0–4 tests.

`./gradlew :app:assembleRelease` **fails as designed** without signing config.

`reviews/` is gitignored. No signing secret in Git. Prefs key name `openweather_api_key` appears as a string; no live API key is embedded.

## Pixel / GrapheneOS checks remaining

After creating the keystore and building `dist/weather-v0.1.0.apk`:

1. Uninstall debug-signed Weather if present
2. Install production APK; confirm signature
3. Version `0.1.0` in Settings
4. Today / Cities / Radar / Settings together
5. Load + Refresh
6. Saved cities + device location
7. Airplane cached Today
8. Reconnect does not thrash when cache is fresh; STALE still recovers
9. Radar tab switches repeatedly
10. Precip/cloud overlays
11. Key save/remove
12. Denied location remains graceful
13. Font scaling usable
14. Visual coherence
15. No crash in daily regression

## GitHub Release readiness

Prepared but **not published**:

* Tag name: `v0.1.0`
* Title: `Weather v0.1.0`
* Assets: `weather-v0.1.0.apk`, `SHA256SUMS`
* Notes: first native Android release; Today instrument; saved + device location; cached offline weather; precip/cloud map (not observed radar); local OpenWeather key; GrapheneOS/Pixel; known limits in `docs/`

## Known limitations

* Maps 1.0 is not observed radar
* No offline map product
* R8 off
* Production APK pending owner keystore
* Uninstall required for debug→production signature change
