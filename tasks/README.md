# Task Execution

Current phase: **Phase 3 — Locations and Offline**

Phase 0 — PWA Foundation, Phase 1 — Weather Data, and Phase 2 — Weather Instrument are accepted and preserved as historical execution evidence.

1. Read `PROJECT.md`.
2. Read `PHASES.md`.
3. Read `TASKS.md`.
4. Read only the current phase task file: `tasks/phase-3.md`.
5. Execute the first incomplete task.
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

Authorized task file:

`tasks/phase-3.md`

Historical task files:

* `tasks/phase-0.md`
* `tasks/phase-1.md`
* `tasks/phase-2.md`

Historical task files are execution evidence.

Do not reopen completed phase work unless Phase 3 explicitly requires a compatible change to existing implementation.

## Source of Truth

When instructions appear to conflict, use this precedence:

1. `PROJECT.md` — product definition and factory rules
2. `PHASES.md` — immutable phase contracts
3. `TASKS.md` — currently authorized phase
4. current `tasks/phase-NUMBER.md` — implementation tasks

Implementation choices must remain inside all higher-level constraints.

## Information Placement

Use:

* `decisions/` for durable choices, constraints, rejections, and compatibility commitments future work must respect
* `docs/` for durable technical knowledge, API behavior, schemas, cache/invalidation rules, setup procedures, visual-system rules, and operational knowledge
* current phase task file for implementation progress, tests, results, changed files, temporary findings, known limitations, and deferred work

If forgetting information could cause a future agent to make a wrong implementation choice, record it.

Otherwise, don't.

## Phase 3 Boundaries

Phase 3 owns:

* manual location search
* saved cities
* active city persistence
* optional device geolocation
* weather caching per location
* stale-state handling
* offline display of last successful data
* recovery when connectivity returns

Phase 3 must not implement:

* radar/maps
* push notifications
* background periodic sync requiring special platform behavior
* severe-weather alert infrastructure
* user accounts
* cloud sync
* location history
* background location tracking

## After Phase Acceptance

Once Phase 3 has been reviewed and explicitly accepted:

* preserve `tasks/phase-3.md` as historical execution evidence
* update `TASKS.md` to authorize the next phase
* update the current phase listed in this file
* create only the newly authorized phase task file

The implementation agent must not perform this transition by itself.
