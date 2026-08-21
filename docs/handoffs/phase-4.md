# Phase 4 handoff (native Android)

Date: 2026-08-21

Accepted: 2026-08-21

Status: **accepted**

Do not begin Phase 5. Do not create `tasks/phase-5.md`.

Native Phase 3 remains accepted. Radar is no longer a placeholder.

## Outcome

Radar is a MapLibre instrument: OpenFreeMap Dark basemap, Phase 3 active-location focus, OpenWeather Maps 1.0 precipitation and cloud-cover overlays (honest names, not observed radar), layer controls, qualitative precip legend, contained failure.

## Implementation summary

- MapLibre Native Android `org.maplibre.gl:android-sdk:11.13.1` (no Play Services, no WebView)
- Basemap: OpenFreeMap Dark `https://tiles.openfreemap.org/styles/dark`
- Overlay: Maps 1.0 `precipitation_new` / `clouds_new` plus NONE
- Active marker + camera from `WeatherSession.active()`; Recenter; no Radar permission prompt
- versionCode 5 / versionName `0.4.0` so overlay install over Phase 3 works

## Map library / source

See `decisions/0021-native-map-stack.md` and `docs/radar.md`.

## Weather data source

OpenWeather Maps 1.0 (free). Not Maps 2.0. Same runtime `ApiKeyStore` key. See `decisions/0022-weather-map-overlay.md`.

## Supported layers

| Control | Semantics |
| --- | --- |
| NONE | Overlay off |
| PRECIP MAP | Precipitation map (model field, ~3 h). Not radar. |
| CLOUD COVER | Cloud-cover map. Not IR satellite. |

## Controls

Hairline chips matching nav chrome. Active overlay bordered. Legend + coordinates on the map. Recenter.

## Active-location integration

Same `Place` as Today/Cities. Switch city then open Radar → camera and marker follow `active`. Device-location active place uses stored coordinates; Radar does not call `LocationManager`.

## Failure behavior

Overlay missing key / add-source failure: basemap remains, note shown. MapView init failure: contained Radar copy. Today/Cities/Settings/cache paths unchanged.

## Offline behavior

Not an offline map product. Today cache still applies. Radar may show empty tiles or MapLibre’s incidental memory cache. No region downloads.

## Privacy / network hosts

| Host | Why |
| --- | --- |
| `tiles.openfreemap.org` | Style, vector tiles, glyphs/sprites |
| `tile.openweathermap.org` | Overlay PNGs (`appid` on overlay only) |
| `api.openweathermap.org` | Unchanged weather/geocoding from Phase 3 |

Viewport tile x/y/z is implied by pan/zoom. No analytics. Coordinates are used locally for camera/marker; they are not sent as a dedicated location API for Radar.

`ACCESS_WIFI_STATE` pulled by MapLibre is **removed** from the merged manifest (`tools:node="remove"`).

## Tests / results

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease
```

`:app:testDebugUnitTest` **PASS** (includes overlay URL/redaction, no-key plan, camera target on location switch, GeoJSON lon/lat, existing Phase 0–3 tests). No live GPS.

## APK paths

- Debug: `app/build/outputs/apk/debug/app-debug.apk` (~77 MB; MapLibre native libs)
- Release-like: `app/build/outputs/apk/release/app-release.apk` (~69 MB, debug-signed)

Permissions: INTERNET, ACCESS_NETWORK_STATE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION. No background location. No ACCESS_WIFI_STATE.

## Known limitations

- OpenWeather Maps 1.0 is **not** observed radar and has no published mm/h legend; precip legend is qualitative
- OpenFreeMap Dark is a public instance; availability is outside this app
- First Radar open downloads style + tiles; airplane mode is not a full map
- MapLibre increases APK size substantially (native .so per ABI)
- Overlay tiles consume the same free-key budget as other OpenWeather calls
- Airplane mode is not a full offline map; previously loaded tiles may remain visible from MapLibre cache

## Deferred work

- Phase 5 polish / accessibility / production signing
- Additional Maps 1.0 layers (temp/wind/pressure) if they prove useful later
- Observed radar if a free, legal, GrapheneOS-friendly source appears
- Broader code audit (still deferred)

## Owner validation (Pixel / GrapheneOS, 2026-08-21)

* basemap renders
* precipitation/cloud layers work
* active saved city focus works
* device-location focus works
* pan/zoom/recenter work
* layer switching works
* airplane mode is graceful; previously loaded tiles may remain visible from MapLibre cache
* Today/Cities remain unaffected
* visual direction accepted

Phase 4 is **accepted**. Do not begin Phase 5.
