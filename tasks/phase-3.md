# Native Phase 3 — Locations and Offline

## Status

**Accepted** 2026-08-21 on Pixel / GrapheneOS.

Owner authorized Native Phase 3 on 2026-08-21. Native Phase 2 remains accepted.

Do not begin Native Phase 4. Do not create `tasks/phase-4.md`.

---

## Objective

Make Weather reliable for daily use: search, saved cities, active location, optional device location, per-location cache, stale/offline, recovery.

---

## Files Changed

- `TASKS.md`, `tasks/README.md`, `tasks/phase-3.md`
- `app/` places, catalog, cache, session, geocoding, Cities, Today status, permissions
- `docs/` locations, cache, geolocation, offline, openweather, android-project
- `docs/handoffs/phase-3.md`, `docs/handoffs/pwa-phase-3.md`
- `decisions/0020-native-place-and-cache-storage.md`
- version `0.3.0` / versionCode 4

---

## Tests Performed

`./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease` — pass.

---

## Results

APKs produced. Permissions: INTERNET, NETWORK_STATE, COARSE+FINE location. No background location. No Radar.

Owner validation on Pixel / GrapheneOS (2026-08-21):

* city search/save/switch — pass
* active city persistence — pass
* saved locations persistence — pass
* device location — pass
* permission denial path — pass
* airplane-mode relaunch with cached weather — pass
* cached/stale indication — pass
* failed refresh preserving cache — pass
* reconnect recovery to LIVE — pass
* rapid city switching without wrong-location repaint — pass
* API key persistence — pass
* Phase 2 Today design remaining intact — pass

---

## Known Limitations

Owner airplane-mode, permission, and race paths were accepted on Pixel / GrapheneOS.

---

## Deferred Work

* Phase 4 Radar/maps
* Production signing

---

## Decisions Created

* `0020-native-place-and-cache-storage.md`

---

# Completion Rule

Stop at handoff. Do not begin Phase 4. Do not create `tasks/phase-4.md`.
