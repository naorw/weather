# Stage 1 — Phase 0: Current Context and Map Semantics

Status: **implemented / awaiting owner acceptance**

Release target: **v0.1.1** (`versionCode` 7)

Base `main`: `e48cc7280fda083d98cfa9899dbe870337dca0e6`

## Goal

On cold start, when location permission is already granted, prefer the device's current location instead of silently showing a stale previously active city. Also rename the existing map surface so it is not presented as observed radar.

## Scope

- On cold app start, if location permission is already granted, resolve device location, make it active for the session, reverse-geocode using the existing path, and refresh weather for it.
- If location lookup fails, fall back safely to the previous active/cached location without erasing cache or claiming that fallback is the current device location.
- If permission is not already granted, do not request it automatically; preserve current saved-location behavior.
- Automatic current-location selection is startup-only. A city the user explicitly activates during the running session wins until the app is restarted.
- Do not auto-save transient device locations into Saved Cities.
- Bottom navigation label becomes `Map`.
- Map screen header becomes `WX MAP`.
- Keep existing `PRECIP MAP` and `CLOUD COVER` terminology and do not call model-derived overlays observed radar.
- Launcher/app display label is `WX Weather` (owner-approved naming polish; package unchanged).
- Prepare `versionName = "0.1.1"`, increment versionCode, and reuse the v0.1.0 production signing identity.

## Explicit Exclusions

- Background location.
- Startup permission prompt.
- Periodic/background refresh.
- Notifications.
- Widgets or watch work.
- New weather providers or paid data.
- Observed or historical radar.
- New map layers.
- Accounts or cloud sync.
- Broad refactor unrelated to this phase.

## Acceptance Criteria

1. With location permission already granted, cold launch resolves current location and Today shows weather for it.
2. Startup does not silently present a stale previous city as current context.
3. Location failure falls back cleanly to the previous active/cached location.
4. Without location permission, startup does not prompt and existing behavior remains usable.
5. Device startup does not create duplicate saved-city entries.
6. Manual city choice wins for the rest of the running session, including after tab navigation.
7. Offline/cache guarantees remain intact.
8. Navigation says `MAP`; map header says `WX MAP`.
9. Existing map overlays remain truthful and functional.
10. v0.1.1 reuses the v0.1.0 signing identity.
11. Existing tests pass and focused startup/session tests are added where practical.

## Execution evidence (2026-09-12)

Implementation is complete locally. Owner Pixel / GrapheneOS acceptance of the full phase is **not** claimed here. Owner **did** approve the launcher label `WX Weather`. v0.1.1 is **not** tagged or published.

### Behavior

- Cold start uses `resolveStartupPlace` only when `SessionPlacePolicy.tryBeginColdStart()` issues a generation (process-scoped; retries after cancelled composition; skipped once `DONE`).
- Permission is read, never requested, at startup. Cities still owns the permission launcher.
- Successful GPS + resolved reverse geocode `setActive`s `PlaceSource.Device` and does not `save`.
- Unresolved reverse (hint name unchanged, swallowed `WeatherError`, empty geo hits), GPS failure, and other lookup exceptions keep the previous place; a leftover `PlaceSource.Device` active row is demoted to `Saved` without adding a saved-city entry.
- `select()` marks a process-scoped manual choice so a late GPS result cannot overwrite it. Later Today visits use `RefreshTrigger.Navigation` only.

### Files changed

- `app/src/main/java/org/radilabs/weather/session/StartupPlace.kt` (new)
- `app/src/main/java/org/radilabs/weather/ui/WeatherRoot.kt`
- `app/src/main/java/org/radilabs/weather/ui/Dest.kt`
- `app/src/main/java/org/radilabs/weather/ui/radar/RadarScreen.kt`
- `app/src/main/java/org/radilabs/weather/location/DeviceLocator.kt`
- `app/build.gradle.kts`
- `app/src/main/res/values/strings.xml` (launcher label `WX Weather`)
- `app/src/test/java/org/radilabs/weather/AppLabelTest.kt` (new)
- `app/src/test/java/org/radilabs/weather/session/StartupPlaceTest.kt` (new)
- `app/src/test/java/org/radilabs/weather/ui/DestTest.kt`
- `app/src/test/java/org/radilabs/weather/persist/PrefsPlaceCatalogTest.kt`
- `docs/locations.md`, `docs/development.md`, `docs/android-project.md`, `docs/radar.md`, `README.md`
- `decisions/0023-startup-device-location.md`
- `docs/handoffs/stage-1-phase-0.md`
- this file

`PHASES.md` not modified.

### Tests / build

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

- `:app:testDebugUnitTest` **PASS** (StartupPlace: permission off, GPS+activate-without-save, GPS fail, Device-source demotion, reverse throw, unresolved reverse hint, generation token, manual catalog override; Dest MAP; Prefs activate-without-save; AppLabel `WX Weather`; existing session race/cache tests).
- `:app:assembleDebug` **PASS**
- `:app:lintDebug` **PASS** (`MissingPermission` suppressed after existing `hasPermission()` check)

### Emulators inspected

AVDs present: `Pixel_9a`, `Pixel_7` under `$HOME/.config/.android/avd`.

- `Pixel_9a` with `-gpu swiftshader_indirect` **SEGV ~25s**; not used for UI checks.
- `Pixel_7` (`emulator-5556`, `-gpu host`) booted. Location setting was already **on** (`location_mode=3`). Package was not preinstalled.

**Pixel_7 — no permission (inspected `granted=false` for FINE/COARSE after clean install):** cold start showed Today Stockholm, nav `MAP`, no permission dialog. Map tab: header `WX MAP`, `PRECIP MAP` / `CLOUD COVER`, legend “Not observed radar”.

**Pixel_7 — permission granted (`pm grant` FINE+COARSE, `emu geo fix` Paris):** cold start did **not** show a permission dialog. No OpenWeather key on the emulator, so reverse stayed unresolved and Today remained Stockholm (contractual geocode-failure fallback). OS “Turn on location” sheet appeared only after `location_mode=0`; dismissed with Close; app still Stockholm. Manual city search was not exercised (needs a key).

Debug APK `application-label:'WX Weather'`; `package: name='org.radilabs.weather'`. Pixel_9a App info heading **WX Weather**.

### Watcher

Independent Bugbot Watcher against this contract.

1. FAIL — cold start token consumed before GPS finished.
2. FAIL — failure left `PlaceSource.Device` on fallback.
3. FAIL — rotation during IN_PROGRESS skipped retry.
4. FAIL — swallowed reverse / hint geocode still activated device coords.
5. **PASS** — “Bugbot found no bugs.” ([Watcher](7df5a2b6-46c6-440e-a207-25aefc5e1842))

Contract was not weakened.

## Required Verification

Test on Pixel / GrapheneOS:

- cold launch with permission already granted;
- location unavailable / failed lookup fallback;
- no permission and no startup prompt;
- manual city selection followed by tab navigation;
- offline fallback behavior;
- Map / WX MAP naming;
- production signing continuity from v0.1.0.

Because this phase changes startup/location lifecycle behavior, obtain an independent reviewer or Watcher pass before owner acceptance.

## Handoff Contract

Before this phase can be declared complete:

- startup location behavior must be demonstrated on a real Pixel / GrapheneOS device;
- no-permission and failed-location fallback paths must be demonstrated;
- manual-selection precedence for the running session must be demonstrated;
- offline/cache behavior must be rechecked;
- map naming must be visually reviewed;
- tests and build results must be recorded;
- signing continuity for v0.1.1 must be verified;
- known limitations and deferred work must be recorded.

Then STOP.

Do not begin Stage 1 Phase 1 automatically.

## Known limitations

- Without a configured OpenWeather key, reverse geocode is unresolved, so granted-permission startup keeps the previous city even if GPS would succeed.
- Emulator UI did not cover named-city GPS success or Cities search (both need a key).
- Pixel_9a swiftshader headless crashed; host GPU works for Pixel_7.
- `lintDebug` still reports dependency/target **warnings**.

## Deferred work

- Owner Pixel / GrapheneOS acceptance
- Tag/publish v0.1.1
- Stage 1 Phase 1+
- Background location, periodic sync, notifications, widgets, new layers/providers, paid data, R8
