# Task Execution

Current phase: **none authorized**

Native Android Phase 0 is defined in `PHASES.md` but is **not** authorized. Do not create `tasks/phase-0.md`. Do not begin native implementation.

Historical `tasks/phase-0.md` … `tasks/phase-3.md` are PWA prototype evidence only.

1. Read `PROJECT.md`.
2. Read `PHASES.md`.
3. Read `TASKS.md`.
4. Read only the currently authorized phase task file, if any.
5. If none is authorized, STOP.
6. Execute the first incomplete task of the authorized phase only.
7. Do not work outside the current phase.
8. Discoveries outside scope go to **Deferred Work** in the current phase task file.
9. A completed task does not imply a completed phase.
10. Phase completion requires satisfying the current phase acceptance criteria and handoff contract in `PHASES.md`.
11. Record decisions under `decisions/` only when future work must respect them.
12. Record durable technical knowledge under `docs/` only when future work needs it.
13. Keep the current phase task file updated with implementation evidence, tests, files changed, known limitations, deferred work, and decisions.
14. Stop at phase handoff.
15. Never create or begin the next phase task file automatically.

## Current Execution

No native phase is authorized.

Historical PWA task files (do not execute):

* `tasks/phase-0.md`
* `tasks/phase-1.md`
* `tasks/phase-2.md`
* `tasks/phase-3.md`

Do not reopen PWA work.

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

## After Phase Authorization

Once the owner/planner explicitly authorizes Native Phase 0:

* create only `tasks/phase-0.md`
* update `TASKS.md` to name that phase
* update the current phase listed in this file

The implementation agent must not perform this authorization by itself.
