# Native Phase 4 — Radar and Maps

## Status

**Accepted** 2026-08-21 on Pixel / GrapheneOS.

Owner authorized Native Phase 4 on 2026-08-21. Native Phase 3 remains accepted.

Do not begin Native Phase 5. Do not create `tasks/phase-5.md`.

---

## Objective

Turn Radar into a map-oriented meteorological instrument: base map, active location, at least one truthful free weather overlay, contained failure.

Derived from the immutable Native Phase 4 contract in `PHASES.md`.

Chosen v1 strategy:

* MapLibre Native (no Play Services)
* OpenFreeMap Dark vector basemap
* OpenWeather Maps 1.0 raster tiles (`precipitation_new`, `clouds_new`) — **not** observed radar

---

## Explicit Exclusions

Paid radar, historical radar, routing, GIS, drawing, offline map downloads, alerts, notifications, AI, Phase 5.

---

## Files Changed

* `TASKS.md`, `tasks/README.md`, `tasks/phase-4.md`, `README.md`
* `app/build.gradle.kts` (0.4.0 / versionCode 5, MapLibre)
* `app/src/main/AndroidManifest.xml` (strip MapLibre `ACCESS_WIFI_STATE`)
* `app/src/main/java/.../map/WeatherOverlay.kt`
* `app/src/main/java/.../ui/radar/RadarScreen.kt`
* `app/src/main/java/.../ui/WeatherRoot.kt`, `ui/settings/SettingsScreen.kt`
* `app/src/test/java/.../map/WeatherOverlayTest.kt`
* `decisions/0021-native-map-stack.md`, `decisions/0022-weather-map-overlay.md`
* `docs/radar.md`, `docs/openweather.md`, `docs/credentials.md`, `docs/offline.md`, `docs/android-project.md`
* `docs/handoffs/phase-4.md`

---

## Tests Performed

`./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease` — PASS (2026-08-21).

---

## Results

Radar instrument accepted on Pixel / GrapheneOS. Not Phase 5.

Owner validation:

* basemap renders
* precipitation/cloud layers work
* active saved city focus works
* device-location focus works
* pan/zoom/recenter work
* layer switching works
* airplane mode is graceful; previously loaded tiles may remain visible from MapLibre cache
* Today/Cities remain unaffected
* visual direction accepted

---

## Known Limitations

See `docs/handoffs/phase-4.md`.

---

## Deferred Work

* Phase 5 polish / accessibility / signing
* Observed radar if a free source appears later
* Extra Maps 1.0 layers

---

## Decisions Created

* `decisions/0021-native-map-stack.md`
* `decisions/0022-weather-map-overlay.md`

---

# Completion Rule

Stop at handoff. Do not begin Phase 5. Do not create `tasks/phase-5.md`.
