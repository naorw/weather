# Weather — Tasks

Detailed implementation tasks live under `tasks/`.

Only the currently authorized phase has a detailed task file that should be executed.

## Current execution

**Stage 1 — Daily-Use Refinement** is authorized.

Current authorized phase:

**Stage 1 / Phase 0 — Current Context and Map Semantics**

Release target: **Weather v0.1.1**.

This phase addresses field-discovered daily-use behavior after the v0.1.0 release:

- prefer current device location on cold start when location permission is already granted;
- do not request location permission automatically on startup;
- fall back safely to the previous active/cached place if current location cannot be resolved;
- let an explicit manual city choice win for the rest of the running session;
- rename the user-facing Radar surface to Map / WX MAP without claiming observed radar.

Detailed task file: `tasks/stage-1-phase-0.md`.

Do not create or execute Stage 1 Phase 1 work. Do not reopen Stage 0.

## Stage 0 — Native v0.1 — completed

Stage 0 produced and released **Weather v0.1.0**.

Completed native phases:

* Native Phase 0 — Android Foundation
* Native Phase 1 — Weather Data
* Native Phase 2 — Weather Instrument
* Native Phase 3 — Locations and Offline
* Native Phase 4 — Radar and Maps
* Native Phase 5 — Daily-Use Polish

Historical task files remain execution evidence under `tasks/phase-0.md` through `tasks/phase-5.md`.

## Historical PWA prototype

Files under `tasks/pwa/` are historical prototype evidence only and must not be executed.

The roadmap and immutable phase contracts live in `PHASES.md`.
The product definition and factory rules live in `PROJECT.md`.
