# Phase 2 handoff (native Android)

Date: 2026-08-21

Accepted: 2026-08-21

Status: **accepted**

Historical PWA Phase 2 handoff is `docs/handoffs/pwa-phase-2.md`.

Do not begin Phase 3. Do not create `tasks/phase-3.md`.

## Outcome

Today is the Weather instrument: live Stockholm data from the unchanged Phase 1 provider, graphite hierarchy (now → 3-hour steps → actual-horizon days → atmosphere), wind compass, OpenWeather 1–5 AQ scale, and in-frame loading/error/refresh.

## Implementation summary

- Phase 1 `OpenWeatherProvider` / models / errors unchanged
- Compose primitives in `ui.instrument` (labels, hairline, range bar, compass, AQ scale)
- 3-hour strip labeled honestly (not hourly)
- Days use whatever horizon Phase 1 aggregated; muted `part.`; shared-scale range bars
- Failed first load: instrument error copy. Failed refresh: keep last in-memory snapshot + amber note (not persisted)
- versionCode 3 / versionName `0.2.0`

## Design-system changes

`docs/design-system.md`: 3-hour wording, Compose primitive list, omitted precip as em dash, refresh vs cache distinction.

## Components added

`Hairline`, `SectionLabel`, `TechnicalLabel`, `RangeBar`/`rangeBarFractions`, `WindCompass`, `AqScale`. Glyphs unchanged in role.

## Tests / build

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease
```

Pass. Phase 1 unit tests kept. Added range-bar normalization, partial-day label, presentation mapping (local 3-hour time, omitted precip, AQ view, glyphs).

## APK paths

- Debug: `app/build/outputs/apk/debug/app-debug.apk` (~28 MB)
- Release-like: `app/build/outputs/apk/release/app-release.apk` (~21 MB)

Permissions unchanged: `INTERNET` only (plus AndroidX dynamic-receiver). No location.

## Accessibility issues

- Content descriptions on temperature, 3-hour columns, day rows (including incomplete coverage), compass, AQ scale, glyphs, refresh
- Touch targets 48 dp on refresh, 3-hour columns, and day rows
- Font scaling / contrast: owner accepted the instrument on Pixel / GrapheneOS; no blocking a11y issues recorded

## Known limitations

- Stockholm remains the only location
- No persistent weather cache
- AQ components (PM2.5 etc.) not listed; index + category only
- Compass is compact, not a full meteorological rose
- Release APK still debug-signed

## Deferred work

- Phase 3 locations / cache / offline
- Production signing

## Owner validation (Pixel / GrapheneOS, 2026-08-21)

1. Live Today loads correctly — pass
2. Key survived upgrade — pass
3. Refresh works — pass
4. Visual hierarchy is accepted — pass
5. 3-hour strip is readable — pass
6. Day range bars / partial-day treatment are accepted — pass
7. Atmospheric details are readable — pass
8. Error/missing-key presentation is acceptable — pass

## Stop

Phase 3 is not authorized by this acceptance. Do not create `tasks/phase-3.md`.
