# Phase 2 — Weather Instrument

## Status

**Accepted** — 2026-08-21.

Owner completed visual review of the live Today instrument.

Do not begin Phase 3 until that phase is explicitly authorized.

---

## Objective

Replace the static Phase 0 Today snapshot with a real weather instrument backed by the normalized Phase 1 data layer.

The primary outcome is:

**real normalized weather data → coherent Today instrument**

Phase 2 owns presentation and interaction for the Today screen.

It does not own locations, offline persistence, radar, alerts, or other later capabilities.

---

## Phase Constraints

Respect all accepted Phase 0 and Phase 1 decisions.

Use the existing:

* normalized weather models
* provider boundary
* canonical internal units
* condition vocabulary
* error model
* design tokens
* application shell

Do not bypass the provider boundary or consume raw OpenWeather payloads in UI code.

Do not implement:

* geolocation
* city search
* saved cities
* persistent weather cache
* radar/maps
* background refresh
* notifications
* weather alerts
* AI commentary
* paid APIs
* historical weather
* decorative animation systems
* generic dashboard/component frameworks

---

## Task 2.1 — Inspect Accepted Foundations

Before implementation:

* read `PROJECT.md`
* read `PHASES.md`
* read `TASKS.md`
* read `tasks/README.md`
* read this file
* inspect Phase 0 design-system docs and decisions
* inspect Phase 1 provider/model docs and decisions
* inspect current Today implementation
* inspect current weather glyph implementation

Confirm that the accepted boundaries remain internally consistent.

Do not reopen accepted decisions without a genuine contradiction.

Record unrelated findings under **Deferred Work**.

---

## Task 2.2 — Define Today Data State

Define the smallest UI state required by the Today screen.

At minimum support:

* idle/initial
* loading
* loaded
* empty/no usable weather
* provider error

The loaded state must contain application-owned normalized weather data only.

Do not introduce a generic global state framework unless genuinely required.

The Today screen should ultimately consume one coherent weather snapshot rather than independently requesting data per component.

---

## Task 2.3 — Bind Real Stockholm Data

Replace the static Today weather content with real data from the existing Phase 1 provider path.

For Phase 2, a fixed **Stockholm** location is acceptable.

Do not add:

* geolocation
* city search
* location settings

The purpose is to prove and polish the actual weather instrument against live normalized data.

Keep location selection deferred to Phase 3.

---

## Task 2.4 — Current Conditions Hero

Implement the primary weather instrument region.

Display at minimum:

* location
* current temperature
* current condition
* high / low
* feels-like
* last update timestamp

Selected supporting metadata may include:

* visibility
* cloud coverage
* air quality
* wind summary

Keep this secondary.

The current temperature and condition must remain visually dominant.

Do not turn the hero into a telemetry dump.

---

## Task 2.5 — Hero Visual Instrument

Add the restrained visual element that gives the current-conditions region its instrument identity.

This may use:

* weather glyph
* grid
* target/radar geometry
* atmospheric linework
* condition-specific technical motif

Requirements:

* subtle
* static or minimally animated only if animation communicates state
* consistent with the existing graphite/industrial design
* must not reduce text readability
* must not become decorative wallpaper

Do not use photographic backgrounds.

Do not use neon/glow-heavy cyberpunk effects.

---

## Task 2.6 — Immediate Forecast Strip

Bind normalized forecast points to the horizontal forecast strip.

Requirements:

* show the nearest useful upcoming forecast points
* display local time correctly
* display temperature
* display condition glyph
* show precipitation probability where useful without clutter
* intentional horizontal scrolling on narrow phones
* practical touch behavior
* no squeezed unreadable columns merely to fit more points

Do not fabricate hourly resolution.

If OpenWeather provides 3-hour points, represent them honestly.

---

## Task 2.7 — Multi-Day Forecast

Bind daily summaries from Phase 1 to the multi-day section.

Display at minimum:

* day
* condition glyph
* precipitation probability where available
* low
* high
* visual temperature range

Requirements:

* support variable number of days
* preserve partial-day semantics
* do not imply an incomplete first/last day is a full-day observation
* no assumption of exactly seven days

Retain the existing temperature-range visual language where it still works.

---

## Task 2.8 — Partial Day Presentation

Define how Phase 1 `partial` daily summaries are presented.

The UI should communicate incomplete forecast coverage subtly.

Possible approaches include:

* small marker
* abbreviated metadata
* restrained technical indicator

Avoid alarming warning styling.

Document the chosen convention if future screens depend on it.

---

## Task 2.9 — Atmospheric Data Section

Bind real atmospheric data to the lower-detail section.

Implement useful modules for available data such as:

* wind
* precipitation
* humidity
* pressure
* visibility
* air quality

Use UV only if normalized Phase 1 data actually contains it.

Requirements:

* values remain readable
* units are explicit
* missing values are distinguishable from zero
* modules should not all compete with equal visual weight
* current conditions and forecast remain more prominent

Avoid generic Material-style card grids.

---

## Task 2.10 — Wind Instrument

Improve wind presentation beyond a plain text value.

At minimum communicate:

* speed
* direction
* gust where available

Use a compact technical representation such as:

* direction arrow
* compass mark
* small directional gauge

Do not build a full meteorological wind visualization system.

That belongs later if needed.

---

## Task 2.11 — Air Quality Presentation

Display the normalized OpenWeather AQ state correctly.

Requirements:

* preserve the normalized human category
* do not mislabel OpenWeather's 1–5 index as EPA AQI or another standard
* raw provider category may be shown as secondary technical metadata if useful
* keep pollutant details below primary weather information

Avoid implying false precision.

---

## Task 2.12 — Weather Glyph Expansion

Expand the Phase 0 glyph set enough to cover the Phase 1 condition vocabulary actually required by real data.

Cover at minimum the useful categories that exist in the normalized condition model.

Requirements:

* consistent stroke/geometry language
* monochrome-first
* readable at forecast-strip size
* no copied proprietary assets
* safe fallback glyph for unknown conditions

Do not create ornamental illustrations.

---

## Task 2.13 — Loading State

Design and implement a loading state that belongs to the same instrument system.

Requirements:

* preserve major layout structure where practical
* avoid generic spinner-only presentation
* no fake weather values
* no excessive animation
* user can distinguish loading from provider failure

The app should feel like an instrument waiting for data rather than a website skeleton template.

---

## Task 2.14 — Empty State

Implement an empty/no-usable-data state.

Use when the application has no valid weather payload to display.

Requirements:

* concise
* technically clear
* visually consistent
* no raw provider JSON/messages
* no invitation to implement Phase 3 location management

Do not confuse empty state with network/provider failure.

---

## Task 2.15 — Provider Error State

Bind the Phase 1 application error model into Today.

Present distinct understandable handling for relevant categories such as:

* authentication/configuration failure
* rate limit
* network unavailable
* timeout
* provider/server failure
* malformed data
* unknown failure

Do not expose:

* API key
* raw provider response bodies
* stack traces

Development diagnostics may remain available in console where safe.

---

## Task 2.16 — Refresh Interaction

Provide a simple explicit refresh action for the Today screen.

Requirements:

* one refresh operation triggers one coherent provider snapshot
* prevent accidental duplicate concurrent refresh storms
* indicate refresh-in-progress
* preserve current valid data while refreshing where practical
* failed refresh must not replace a valid visible state with garbage

Do not implement:

* scheduled refresh
* background refresh
* persistent cache
* pull-to-refresh if it introduces unnecessary complexity

Those remain later concerns.

---

## Task 2.17 — Remove Phase 1 Diagnostic UI

Once the Today screen provides the proper live-data integration, remove or demote any Phase 1-only diagnostic controls that are no longer appropriate for normal application use.

The Settings screen should not remain a developer weather-fetch console.

Keep only genuinely useful Phase 0/Phase 1 proof tooling if still required for development, preferably behind a development-only path.

Do not implement actual Settings functionality yet.

---

## Task 2.18 — Typography and Hierarchy Review

Review the real-data screen against the accepted design system.

Ensure:

* current temperature remains dominant
* location and condition remain easy to scan
* technical labels are secondary
* section hierarchy is obvious
* atmospheric details remain below forecast priority
* dense data does not collapse into visual noise

Avoid increasing information density merely because more data now exists.

---

## Task 2.19 — Responsive Review

Validate the real Today screen at representative portrait widths.

At minimum:

* 360 px
* 393 px
* 412 px

Verify:

* no horizontal page overflow
* forecast strip scrolls intentionally
* hero retains presence
* range bars remain readable
* technical labels do not collide
* bottom navigation remains usable
* atmospheric data does not become cramped

Also verify the actual Pixel viewport.

---

## Task 2.20 — Font Scaling and Accessibility

Validate practical increased font scaling.

At minimum ensure:

* hero values do not overlap
* forecast rows remain understandable
* buttons retain usable touch targets
* labels are not clipped
* condition glyphs have accessible text
* refresh controls have accessible names
* weather information is not conveyed by color alone

Record limitations that belong to later polish.

---

## Task 2.21 — Real-Data Edge Cases

Test the Today UI against fixtures or controlled models representing conditions such as:

* clear
* overcast
* rain
* heavy rain
* snow
* fog/mist
* thunderstorm
* strong wind
* missing gust
* missing precipitation
* missing visibility
* incomplete first/last forecast day

The screen must degrade cleanly without layout collapse.

Do not rely solely on whatever Stockholm happens to be doing today.

---

## Task 2.22 — Component Boundaries

Refactor Today implementation only where needed to keep major sections understandable.

Reasonable boundaries may include:

* current conditions
* hourly forecast
* daily forecast
* atmospheric details
* weather glyphs
* status/error presentation

Avoid creating a generic design-system framework or tiny component explosion.

The code should mirror the product hierarchy.

---

## Task 2.23 — Design-System Documentation

Update the design-system documentation with any Phase 2 rules that future screens must respect.

Document at minimum where relevant:

* real-data typography behavior
* weather glyph conventions
* range-bar semantics
* partial-day indicator
* error/loading visual language
* atmospheric module conventions
* hero instrument treatment

Do not rewrite accepted Phase 0 design history.

Extend it.

---

## Task 2.24 — Tests

Add or update tests for UI logic that materially matters.

At minimum cover:

* loaded Today state
* loading state
* empty state
* provider error state
* variable forecast-day count
* partial-day presentation
* missing optional weather fields
* condition-to-glyph mapping
* refresh state behavior

Do not write brittle tests asserting every decorative DOM detail.

Keep Phase 1 provider/transformation tests passing.

---

## Task 2.25 — Live Android Validation

Validate the Today screen on the actual Pixel/GrapheneOS device.

Verify:

* live Stockholm weather loads
* standalone PWA launch works
* hero hierarchy looks correct
* hourly strip is practical to scroll
* multi-day forecast is readable
* atmosphere data remains legible
* refresh works
* loading/error behavior is understandable
* no obvious browser chrome/layout regression

Capture screenshots or written visual evidence for handoff.

---

## Task 2.26 — Phase 2 Verification

Perform a complete Phase 2 verification pass.

Verify every Phase 2 acceptance criterion in `PHASES.md`.

At minimum record:

* dependency/install result
* lint result
* automated test result
* production build result
* live current weather render
* live forecast render
* atmospheric data render
* loading state
* empty state
* provider-error state
* responsive viewport checks
* font scaling check
* actual Pixel visual review
* Phase 1 regression check

Explicitly confirm that none of the following were implemented:

* geolocation
* city search
* saved cities
* persistent weather caching
* radar/maps
* notifications
* weather alerts
* background refresh
* paid APIs
* AI commentary
* Phase 3 functionality

---

# Expected Repository Artifacts

By the end of Phase 2, the repository should contain or equivalent:

* real-data Today screen
* current-conditions hero
* live forecast strip
* live multi-day forecast
* atmospheric data presentation
* expanded weather glyph set
* loading state
* empty state
* provider-error state
* explicit refresh interaction
* relevant UI tests
* updated design-system documentation
* Phase 2 handoff evidence
* decisions only where future work must respect them

Do not create `tasks/phase-3.md`.

---

# Handoff Notes

## Files Changed

* `src/today/state.ts`, `src/today/present.ts`, `src/today/controller.ts`
* `src/screens/today.ts`, `src/screens/settings.ts`, `src/app.ts`, `src/glyphs.ts`
* `src/styles/today.css`
* `tests/today/**`
* `docs/design-system.md`, `docs/handoffs/phase-2.md`
* `decisions/0011-partial-day-marker.md`

## Tests Performed

```sh
npm test     # 13 files, 55 passed
npm run lint # passed
npm run build # passed
```

## Results

Automated UI and Phase 1 tests: **pass**.

Live Today visual review: **pass** (owner, 2026-08-21).

Phase 2: **accepted**.

## Android Visual Review

2026-08-21: owner visual test of live Today. Accepted.

## States Demonstrated

* loading — “Acquiring weather.” (automated + initial Today bind)
* loaded — snapshot render tests
* empty — “No usable weather payload.”
* provider error — credentials/rate-limit copy without secrets
* refresh failure — last snapshot retained

## Known Limitations

* Location is fixed Stockholm.
* Forecast steps are 3-hour, labeled honestly as “Next 3 hours”.
* Hero high/low use today’s aggregated day when present (not a true instantaneous min/max).
* UV is still absent (Phase 1).
* Production hosts still call OpenWeather directly (dev uses `/ow` proxy).

## Deferred Work

* Geolocation, search, saved cities — Phase 3
* Persistent weather cache / stale markers — Phase 3
* Radar — Phase 4
* Pull-to-refresh, scheduled refresh
* Settings units UI
* Pollutant-detail expansion

## Decisions Created

* `decisions/0011-partial-day-marker.md`

---

# Completion Rule

Phase 2 is complete only when the Phase 2 acceptance criteria and handoff contract in `PHASES.md` are satisfied.

Phase 2 is accepted.

**STOP. Do not begin Phase 3 until it is explicitly authorized.**
