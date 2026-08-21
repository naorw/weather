# Task Execution

Current phase: **none authorized after Native Phase 0**

Native Phase 0 — Android Foundation is **accepted** (2026-08-21). Evidence: `tasks/phase-0.md`.

Do not create `tasks/phase-1.md`. Do not begin Native Phase 1.

Historical `tasks/pwa/phase-0.md` … `tasks/pwa/phase-3.md` are PWA prototype evidence only. Do not execute them.

1. Read `PROJECT.md`.
2. Read `PHASES.md`.
3. Read `TASKS.md`.
4. Read only the currently authorized phase task file, if any.
5. If none is authorized, STOP.
6. Do not work outside the current phase.
7. Discoveries outside scope go to **Deferred Work** in the current phase task file.
8. A completed task does not imply a completed phase.
9. Phase completion requires satisfying the current phase acceptance criteria and handoff contract in `PHASES.md`.
10. Record decisions under `decisions/` only when future work must respect them.
11. Record durable technical knowledge under `docs/` only when future work needs it.
12. Keep the current phase task file updated with implementation evidence, tests, files changed, known limitations, deferred work, and decisions.
13. Stop at phase handoff.
14. Never create or begin the next phase task file automatically.

## Current Execution

No native phase is authorized after Native Phase 0 acceptance.

Historical native task files:

* `tasks/phase-0.md`

Historical PWA task files (do not execute):

* `tasks/pwa/phase-0.md`
* `tasks/pwa/phase-1.md`
* `tasks/pwa/phase-2.md`
* `tasks/pwa/phase-3.md`

Do not reopen PWA work.

Do not reopen completed native phase work unless the current authorized phase explicitly requires a compatible change.

## Source of Truth

When instructions appear to conflict, use this precedence:

1. `PROJECT.md` — product definition and factory rules
2. `PHASES.md` — immutable phase contracts
3. `TASKS.md` — currently authorized phase
4. current `tasks/phase-NUMBER.md` — implementation tasks

Implementation choices must remain inside all higher-level constraints.

Native Android (`decisions/0017-native-android-platform.md`) governs v1. Historical PWA decisions remain in `decisions/` but are superseded where that record says so.

## Information Placement

Use:

* `decisions/` for durable choices, constraints, rejections, and compatibility commitments future work must respect
* `docs/` for durable technical knowledge, API behavior, schemas, cache/invalidation rules, setup procedures, visual-system rules, and operational knowledge
* current phase task file for implementation progress, tests, results, changed files, temporary findings, known limitations, and deferred work

If forgetting information could cause a future agent to make a wrong implementation choice, record it.

Otherwise, don't.

## Phase 0 Boundaries

Phase 0 owns:

* Kotlin + Jetpack Compose application
* APK build/install/debug workflow
* application identity
* static Today design-language baseline
* navigation shell
* local persistence proof
* runtime API-key configuration (no network)

Phase 0 must not implement:

* OpenWeather network calls
* geolocation
* city search / saved cities
* weather cache
* radar/maps
* notifications / widgets / WorkManager
* accounts / analytics / AI / cloud sync

## After Phase Authorization

Once the owner/planner explicitly authorizes Native Phase 1:

* create only `tasks/phase-1.md`
* update `TASKS.md` to name that phase
* update the current phase listed in this file

The implementation agent must not perform this authorization by itself.
