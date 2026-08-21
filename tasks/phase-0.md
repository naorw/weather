# Phase 0 — PWA Foundation

## Status

**Accepted** — 2026-08-21.

Owner verified locally and on a Pixel phone. Phase 0 handoff is closed.

Do not begin Phase 1 until that phase is explicitly authorized.

---

## Objective

Create the smallest working Weather PWA and establish the development, build, install, storage, and visual-system foundation required by later phases.

No real weather-provider integration belongs in this phase.

---

## Required Decisions

Recorded under `decisions/`:

* `0001-application-identity.md`
* `0002-frontend-toolchain.md`
* `0003-indexeddb-access.md`
* `0004-pwa-service-worker.md`
* `0005-font-strategy.md`
* `0006-design-token-structure.md`

---

## Task 0.1 — Bootstrap the Application

Done.

Selected stack:

* Vite 8
* TypeScript (vanilla, no UI framework)
* Vitest + happy-dom
* ESLint + typescript-eslint
* `vite-plugin-pwa` for the service worker (build only)

Commands:

* install: `npm install`
* development: `npm run dev`
* production build: `npm run build`
* preview: `npm run preview`
* test: `npm test`
* lint: `npm run lint`

Requires Node.js 22+.

### Evidence

* `package.json`, `vite.config.ts`, `tsconfig.json`, `eslint.config.js`, `vitest.config.ts`, `index.html`
* `npm test` — 12 passed
* `npm run lint` — clean
* `npm run build` — success; Workbox precache 13 entries

---

## Task 0.2 — Establish Application Identity

Done.

* name: Weather
* short name: Weather
* id: `org.radilabs.weather`
* description: A weather instrument for Android. Current conditions, forecast, and atmospheric detail.
* version: `0.0.0`
* theme/background: `#14171b`
* icons: `public/icons/weather-192.png`, `weather-512.png`, `weather-512-maskable.png`; SVG source `icons/weather.svg`

Decision: `decisions/0001-application-identity.md`

---

## Task 0.3 — PWA Foundation

Done (artifacts). Device install still for the owner.

* `public/manifest.webmanifest` — `display: standalone`, `start_url: ./`
* viewport: `width=device-width, initial-scale=1, viewport-fit=cover`
* Workbox `generateSW` via `vite-plugin-pwa`
* `dist/sw.js` generated on build
* application-shell precache only (no weather payloads)

---

## Task 0.4 — Main Application Shell

Done.

Hash routes: `#/today` `#/radar` `#/cities` `#/settings`

Bottom nav, `aria-current="page"` on the active destination.

Radar / Cities / Settings are placeholders. Settings includes the IndexedDB probe only.

---

## Task 0.5 — Establish the Design System

Done.

Tokens: `src/styles/tokens.css`

Human-readable rules: `docs/design-system.md`

Decision: `decisions/0006-design-token-structure.md`

---

## Task 0.6 — Static Today Screen

Done.

Fake Stockholm snapshot in `src/fake-today.ts` (8 hourly points, **5** days — not hard-coded to 7).

Hierarchy: dominant hero → horizontal hours → day rows with range bar → telemetry list (not equal cards).

---

## Task 0.7 — Weather Glyph Foundation

Done.

Monochrome stroke SVGs in `src/glyphs.ts`: clear, partly-cloudy, overcast, drizzle, rain.

---

## Task 0.8 — Local Structured Storage Proof

Done.

`src/storage.ts` — native IndexedDB, db `org.radilabs.weather.phase0`, store `kv`.

Automated CRUD + reset tests in `tests/storage.test.ts`.

Settings UI: Run storage probe / Reset proof store.

Decision: `decisions/0003-indexeddb-access.md`

---

## Task 0.9 — Responsive and Android Viewport Validation

Done.

CSS: `overflow-x: hidden` on page; hourly strip `overflow-x: auto`; `--touch-min: 48px`; shell `max-width: 40rem`.

Layout uses fluid type (`clamp`) and CSS grid that fits 360–412px.

Owner confirmed usable layout locally and on a Pixel phone (2026-08-21).

---

## Task 0.10 — Accessibility Baseline

Done as a baseline, not a certification.

* `lang="en"`, `nav aria-label="Primary"`
* Today location is `h1`; other screens have `h1`
* Buttons have visible text
* Focus-visible outline
* Precip shown as numbers, not color alone
* Tabular numbers; labels not color-only

Increased system text size was not measured on a physical device.

---

## Task 0.11 — Developer Documentation

Done.

* `docs/development.md`
* `docs/design-system.md`
* `README.md` quick start

---

## Task 0.12 — Phase 0 Verification

### Acceptance criteria (`PHASES.md`)

| # | Criterion | Result |
| --- | --- | --- |
| 1 | Application runs locally | `npm run dev` configured; modules compile |
| 2 | Production build succeeds | `npm run build` passed 2026-08-20 |
| 3 | Installable PWA | Manifest + 192/512 icons + SW generated. Owner tested on Pixel (2026-08-21). |
| 4 | Standalone launch | Manifest `display: standalone`. Covered by owner Pixel test. |
| 5 | Bottom navigation | Implemented; unit tests for routes |
| 6 | Today visual direction | Static instrument layout + tokens |
| 7 | Android portrait sizes | Owner confirmed on Pixel (2026-08-21) |
| 8 | IndexedDB proof | Tests passed |
| 9 | Offline app shell after first load | SW precaches shell. Covered by owner Pixel test. |
| 10 | Build/run/debug docs | `docs/development.md` |
| 11 | No real weather-provider integration | `tests/scope.test.ts` passed; no OpenWeather in `src/` |
| 12 | No later-phase functionality | No geolocation, city search, radar map, weather cache, AI, analytics |

### Explicit absences checked

* OpenWeather network calls — none
* API credentials — none
* geolocation — none
* city search / saved cities — none
* real weather data — fake snapshot only
* radar/map implementation — copy placeholder only
* weather-data offline cache — none
* AI — none
* analytics — none

---

# Expected Repository Artifacts

Present:

* `src/` application source
* `public/manifest.webmanifest`
* generated `dist/sw.js` (build)
* `public/icons/` and `icons/`
* `src/styles/tokens.css`
* Today + placeholder screens
* IndexedDB proof
* `tests/`
* `docs/development.md`, `docs/design-system.md`, `docs/handoffs/phase-0.md`
* `decisions/0001`–`0006`

---

# Handoff Notes

## Files Changed

* `package.json`, `package-lock.json`, `vite.config.ts`, `vitest.config.ts`, `tsconfig.json`, `eslint.config.js`, `index.html`, `.gitignore`
* `src/**` application modules and styles
* `public/manifest.webmanifest`, `public/icons/*`
* `icons/weather.svg` and PNG copies
* `tests/**`
* `docs/development.md`, `docs/design-system.md`, `docs/handoffs/phase-0.md`
* `docs/handoffs/.gitkeep` retained
* `decisions/0001`–`0006`
* `tasks/README.md`, `tasks/phase-0.md`, `README.md`

## Tests Performed

```sh
npm test     # 5 files, 12 tests, all passed
npm run lint # passed
npm run build # passed; PWA generateSW, 13 precache entries
```

Owner manual checks (2026-08-21): local run and Pixel phone — accepted.

## Results

Automated Phase 0 checks: **pass**.

Owner device check: **pass** (Pixel, 2026-08-21).

Phase 0: **accepted**.

## Known Limitations

* Weather values are static fiction (Stockholm).
* Radar, cities, and settings have no product behavior beyond the storage probe.
* No webfont; appearance depends on the device UI typeface.
* `npm run dev` does not exercise the production service worker.
* Icon mark is a simple geometric glyph, not a finished brand lockup.
* Warning token exists but is unused in the static UI.
* Duplicate icon files live in both `icons/` and `public/icons/` so Vite can serve them without extra copy steps.

## Deferred Work

* OpenWeather integration and credential strategy — Phase 1
* Real Today design with live data — Phase 2
* Saved cities, geolocation, weather cache, stale-state — Phase 3
* Radar/map layers — Phase 4
* Production polish, icon set finish, font evaluation on GrapheneOS — Phase 5
* Hash routing vs History API if a host requires cleaner URLs
* Whether to replace vanilla DOM rendering if UI complexity grows

## Decisions Created

* `decisions/0001-application-identity.md`
* `decisions/0002-frontend-toolchain.md`
* `decisions/0003-indexeddb-access.md`
* `decisions/0004-pwa-service-worker.md`
* `decisions/0005-font-strategy.md`
* `decisions/0006-design-token-structure.md`

---

# Completion Rule

Phase 0 is complete only when the Phase 0 acceptance criteria and handoff contract in `PHASES.md` are satisfied.

Phase 0 is accepted.

**STOP. Do not begin Phase 1 until it is explicitly authorized.**
