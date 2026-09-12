# Stage 1 / Phase 0 handoff — Current Context and Map Semantics

Date: 2026-09-12

Status: **implemented / awaiting owner acceptance**

Release candidate: **Weather v0.1.1** (`versionName` 0.1.1, `versionCode` 7). Package `org.radilabs.weather`. Production signing configuration unchanged (gitignored `local.properties` + existing keystore). **Not tagged. Not published. Not owner-accepted.**

Base `main`: `e48cc7280fda083d98cfa9899dbe870337dca0e6`

Do not begin Stage 1 Phase 1.

## What changed

Cold start, when location permission is **already granted**, takes a one-shot device fix, reverse-geocodes through the existing provider path, `setActive`s that `PlaceSource.Device` place (not `save`), and refreshes Today. Permission is never requested at startup. Lookup/geocode failure keeps the previous active place and cache. Device lookup is process-startup-only; an explicit Cities selection marks a process-scoped manual choice so a late GPS result cannot overwrite it. Tab returns to Today use `RefreshTrigger.Navigation` and do not re-resolve GPS.

Bottom nav label is **Map** (rendered `MAP`). Map header is **WX MAP**. Overlay copy `PRECIP MAP` / `CLOUD COVER` and “Not observed radar” remain.

Owner-approved launcher/app display label is **WX Weather** (`@string/app_name`). Package remains `org.radilabs.weather`. GitHub release title remains **Weather v0.1.1**. In-app masthead remains **WX WEATHER**.

## Files changed

- `app/src/main/java/org/radilabs/weather/session/StartupPlace.kt` (new)
- `app/src/main/java/org/radilabs/weather/ui/WeatherRoot.kt`
- `app/src/main/java/org/radilabs/weather/ui/Dest.kt`
- `app/src/main/java/org/radilabs/weather/ui/radar/RadarScreen.kt`
- `app/src/main/java/org/radilabs/weather/location/DeviceLocator.kt` (lint: `@SuppressLint("MissingPermission")` after existing `hasPermission()` check)
- `app/build.gradle.kts` (0.1.1 / 7; dist name `weather-v0.1.1.apk`)
- `app/src/main/res/values/strings.xml` (launcher/app label `WX Weather`)
- tests: `StartupPlaceTest.kt`, `DestTest.kt`, `PrefsPlaceCatalogTest.kt`, `AppLabelTest.kt`
- docs: `docs/locations.md`, `docs/development.md`, `docs/android-project.md`, `docs/radar.md`, `README.md`
- `decisions/0023-startup-device-location.md`
- `tasks/stage-1-phase-0.md`, this handoff

`PHASES.md` was not modified.

## Tests / build

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

- Unit tests: **PASS** (2026-09-12), including 7 `StartupPlaceTest` cases, Dest MAP label, Prefs activate-without-save, existing `WeatherSession` race/cache tests.
- Debug APK: **PASS** `app/build/outputs/apk/debug/app-debug.apk`
- `:app:lintDebug`: **PASS** after documenting the existing location API permission check. 30 dependency/target warnings remain (not treated as this phase’s contract).

## Emulator / device checks

AVDs: `Pixel_9a`, `Pixel_7` (`$HOME/.config/.android/avd`). `-gpu swiftshader_indirect` SEGV on Pixel_9a; **`-gpu host`** used.

**Pixel_7 (`emulator-5556`)** — inspected `ACCESS_*_LOCATION granted=false` after clean install. Cold start: Stockholm, nav `MAP`, no permission dialog. Map: `WX MAP`, `PRECIP MAP` / `CLOUD COVER`, “Not observed radar”. After `pm grant` + Paris `geo fix`: no permission dialog; no API key so reverse unresolved and Today stayed Stockholm. `location_mode=0` showed the **OS** location sheet (not an app permission prompt); after Close, Today still Stockholm.

**Pixel_9a (`emulator-5554`, host GPU)** — used for the permission-granted install path (`pm grant` before first launch). See task file.

Real Pixel / GrapheneOS owner acceptance is after this handoff.

## Signing

`applicationId` unchanged. Release signing block unchanged; no keys generated or replaced. v0.1.1 production APK / tag not produced in this phase.

## Watcher

Independent Bugbot Watcher vs `tasks/stage-1-phase-0.md`. Four in-scope FAILs were fixed (startup generation token, Device-source demotion, rotation retry, unresolved reverse). Final result: **PASS** (“Bugbot found no bugs.”) [Watcher](7df5a2b6-46c6-440e-a207-25aefc5e1842).

## Known limitations

- Reverse geocode still depends on a configured OpenWeather key; without a key the device place may activate with the coordinate hint name `Device location`.
- MapLibre / overlay behavior is unchanged besides labels.
- Lint `MissingPermission` is suppressed after an explicit runtime permission check; Android lint cannot see that check.

## Deferred

- Stage 1 Phase 1 and later (`PHASES.md`)
- Background location, periodic sync, notifications, widgets
- New providers, paid data, new map layers, observed radar
- Publish/tag v0.1.1
- R8/minify (still off)
- Owner Pixel / GrapheneOS acceptance
