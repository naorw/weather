# Native Phase 2 — Weather Instrument

## Status

**Accepted** 2026-08-21 on Pixel / GrapheneOS.

Owner authorized Native Phase 2 on 2026-08-21. Native Phase 1 remains accepted.

Do not begin Native Phase 3. Do not create `tasks/phase-3.md`.

---

## Objective

Turn the live Today screen into the Weather product: hierarchy, information design, interaction, visual language, and real-data presentation.

Preserve the Phase 1 provider/data layer.

---

## Files Changed

- `TASKS.md`, `tasks/README.md`, `tasks/phase-2.md`
- `app/src/main/java/org/radilabs/weather/ui/instrument/`
- `app/src/main/java/org/radilabs/weather/ui/today/`
- `app/src/main/java/org/radilabs/weather/ui/WeatherRoot.kt`
- `app/src/test/java/org/radilabs/weather/ui/`
- `docs/design-system.md`, `docs/android-project.md`, `docs/handoffs/phase-2.md`, `docs/handoffs/pwa-phase-2.md`
- version `0.2.0` / versionCode 3

---

## Tests Performed

`./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease`

Phase 1 weather tests plus range-bar, partial-day, presentation tests. Pass.

---

## Results

Debug and release-like APKs produced. Manifest still `INTERNET` only. No Phase 3 locations/cache.

Owner validation on Pixel / GrapheneOS (2026-08-21):

* live Today loads correctly — pass
* key survived upgrade — pass
* refresh works — pass
* visual hierarchy is accepted — pass
* 3-hour strip is readable — pass
* day range bars / partial-day treatment are accepted — pass
* atmospheric details are readable — pass
* error/missing-key presentation is acceptable — pass

---

## Known Limitations

- Device visual/font-scale: accepted on Pixel / GrapheneOS; no blocking issues recorded
- In-memory last snapshot only
- AQ components not listed

---

## Deferred Work

* Phase 3 locations / cache / offline
* Production signing

---

## Decisions Created

None. Existing `0005`, `0007`, `0010`, `0011`, `0017`–`0019` still apply.

---

# Completion Rule

Stop at handoff. Do not begin Phase 3. Do not create `tasks/phase-3.md`.
