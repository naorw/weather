# Phase 3 — Locations and Offline

## Status

**Stopped / superseded by native Android platform decision** (`decisions/0017-native-android-platform.md`).

This file is historical PWA prototype evidence. Do not execute it.

Significant Phase 3 behavior was implemented in the PWA (search, saved/active cities, optional geolocation, per-location cache, stale/fresh, recovery). The owner did not complete final PWA device acceptance. The PWA implementation is intentionally abandoned rather than completed. PWA Phase 4 was never started.

Native phases restart at Phase 0 in `PHASES.md`. Native Phase 0 is not authorized by this file.

---

## Objective

Make Weather dependable for normal daily use.

The application must allow the user to choose locations, save them, optionally use device location, and continue showing the latest useful weather during temporary loss of connectivity.

The primary outcome is:

**location selection + durable local state + cached weather + explicit staleness + clean recovery**

Phase 3 does not own maps, notifications, background sync, accounts, or cloud state.

---

## Phase Constraints

Respect accepted Phase 0–2 architecture and design decisions.

Use:

* the existing `WeatherProvider` boundary
* normalized weather models
* Today controller/state conventions
* IndexedDB foundation
* existing design system
* existing application shell

Do not introduce a backend or account system.

Do not add:

* radar/maps
* push notifications
* background periodic sync
* severe-weather alert infrastructure
* cloud synchronization
* location history
* background location tracking

---

## Task 3.1 — Inspect Accepted Foundations

Before implementation:

* read `PROJECT.md`
* read `PHASES.md`
* read `TASKS.md`
* read `tasks/README.md`
* read this file
* inspect Phase 0–2 decisions
* inspect storage docs and IndexedDB implementation
* inspect provider/geocoding capabilities
* inspect Today controller and navigation

Confirm no blocking contradiction.

Do not reopen accepted decisions without a real incompatibility.

Record unrelated discoveries under **Deferred Work**.

---

## Task 3.2 — Define Location Model

Define the application-owned location model used by Cities, Today, storage, and cache lookup.

At minimum support:

* stable local ID
* display name
* latitude
* longitude
* country code
* state/region where supplied
* optional locality metadata needed to distinguish duplicates
* source/type where useful, such as saved or device-derived

The model must not expose raw OpenWeather geocoding payloads outside the provider adapter.

---

## Task 3.3 — Implement Geocoding Provider Boundary

Extend the provider boundary with the minimum location-search capability required by Phase 3.

Use OpenWeather geocoding/free capability where appropriate.

Support:

* query by city/place name
* normalized location results
* enough context to distinguish similarly named places

Do not build a generic multi-provider geocoder framework.

---

## Task 3.4 — Location Search UI

Implement manual search under **Cities**.

Requirements:

* user can enter a place name
* search is explicit or sensibly debounced without request spam
* loading state is visible
* no-results state is clear
* provider/search errors are understandable
* results show enough context to distinguish duplicates
* selecting a result can make it active
* selecting/saving a result must not create accidental duplicates

Keep the existing technical visual language.

Do not turn Cities into a generic settings form.

---

## Task 3.5 — Saved Cities Persistence

Persist saved locations in IndexedDB.

Support:

* save city
* remove city
* list saved cities
* preserve ordering where practical
* survive reload/restart/PWA relaunch

Define deterministic duplicate semantics.

At minimum, locations representing the same coordinate/place must not multiply because search was repeated.

Document the chosen identity/deduplication rule if future work depends on it.

---

## Task 3.6 — Active Location

Persist the currently active location.

Requirements:

* switching city updates Today
* active location survives reload/restart
* removal of the active city has deterministic fallback behavior
* corrupt/missing active-location references recover safely
* no hard-coded Stockholm dependency remains in normal operation

A sensible first-run default may still be Stockholm if required, but document it explicitly.

---

## Task 3.7 — Device Geolocation

Add optional browser geolocation.

Requirements:

* initiated by an explicit understandable user action
* permission is never requested silently on startup
* permission denial is handled gracefully
* timeout/unavailable position is handled
* Weather remains fully usable without location permission
* no background tracking
* no location history

Use the returned coordinates to obtain weather directly.

Reverse geocoding may be used only if needed to produce a reasonable display name.

---

## Task 3.8 — Geolocation Permission States

Handle relevant browser/device states:

* granted
* denied
* prompt/not-yet-decided
* unavailable/unsupported
* timeout
* insecure-context restriction where applicable

UI must not repeatedly nag after denial.

Document browser/PWA limitations discovered on GrapheneOS/Vanadium and Firefox.

---

## Task 3.9 — Define Weather Cache Schema

Create a persistent cache schema for the latest successful normalized weather snapshot per location.

Persist enough metadata to determine:

* location identity
* snapshot data
* retrieval time
* provider
* cache/schema version
* age/staleness

Do not store raw OpenWeather responses unless there is an explicit documented reason.

The cache should contain application-owned normalized data.

Document schema and migration/invalidation strategy.

---

## Task 3.10 — Cache Successful Weather

Whenever a weather refresh succeeds:

* update the latest snapshot for that location
* preserve retrieval timestamp
* avoid duplicate cache records
* never replace a valid cached snapshot with a failed/invalid result

Caching must be per location.

Switching cities must not show another city's cached data.

---

## Task 3.11 — Define Staleness Rules

Define explicit staleness semantics.

At minimum distinguish:

* fresh/current-enough
* stale but useful
* unavailable/no cache

Choose concrete age thresholds appropriate for weather data.

The UI must communicate age honestly.

Avoid pretending cached data is live.

Document the rule under `docs/`, and create a decision record if future phases must respect it.

---

## Task 3.12 — Today Cached Startup

Change Today startup behavior so it can use cached data.

Desired flow:

1. resolve active location
2. load cached snapshot if available
3. display it immediately with age/stale state as appropriate
4. attempt live refresh
5. replace cache/display only after successful refresh

Do not blank the screen merely because the provider is temporarily unreachable.

---

## Task 3.13 — Offline Behavior

When network access is unavailable:

* application shell loads
* active location resolves
* last successful cached weather displays if available
* stale state is obvious
* age of cached data is understandable
* no repeated request storm occurs
* failure does not erase cached data

If there is no cache, show the existing empty/offline state clearly.

---

## Task 3.14 — Stale Visual Treatment

Add restrained stale-data treatment to Today.

Requirements:

* visibly distinguish cached/stale from live/current
* show last successful update age/time
* use the existing graphite/amber technical language
* avoid alarm styling for ordinary temporary offline use
* do not obscure the weather values

Example vocabulary may include:

* `CACHED`
* `STALE`
* `UPDATED 47 MIN AGO`

Exact wording is an implementation decision.

---

## Task 3.15 — Refresh Recovery

When connectivity returns or the user manually refreshes:

* request fresh weather for the current location
* update the visible snapshot only on success
* update the cache
* clear stale indication only after success
* preserve cached data on failure
* avoid concurrent duplicate refreshes

Switching location during an in-flight request must not write/show that response against the wrong location.

---

## Task 3.16 — Network Recovery Behavior

Support clean recovery without implementing background periodic sync.

Reasonable behavior may include:

* explicit refresh
* one refresh when the browser reports returning online
* normal refresh when Today becomes active

Do not create a timer/polling scheduler.

If using the browser `online` event, guard against duplicate requests.

---

## Task 3.17 — Cities Screen

Turn the Cities placeholder into the Phase 3 location-management screen.

Display:

* active location
* saved locations
* search affordance
* device-location affordance

Support:

* switch
* save
* remove

Keep interactions touch-friendly and visually consistent with the weather instrument.

Avoid card soup.

---

## Task 3.18 — First-Run / Empty Location Behavior

Define behavior when no saved or active location exists.

User must be able to:

* search manually
* optionally use device location

Do not force geolocation permission.

Do not leave Today in an unrecoverable empty state.

---

## Task 3.19 — Settings Scope Check

Phase 3 may expose only settings directly required by this phase if useful, such as location-related behavior.

Do not opportunistically implement the full Settings roadmap.

Display-unit settings, generic refresh scheduling, provider configuration UI, and unrelated preferences remain deferred unless the current contract requires them.

---

## Task 3.20 — Storage Integrity

Handle invalid/corrupt local state safely.

At minimum test:

* malformed saved-location record
* missing active location
* duplicate saved locations
* malformed cached snapshot
* incompatible cache/schema version

Bad local state must not crash the application.

Prefer dropping one invalid record over destroying unrelated valid state.

---

## Task 3.21 — Cache/Location Tests

Add deterministic tests for:

* saving locations
* removing locations
* location deduplication
* active-location persistence
* active-location fallback
* cache write/read
* per-location cache isolation
* stale/fresh classification
* failed refresh preserving cache
* successful refresh replacing cache
* corrupt-record recovery

---

## Task 3.22 — Geolocation Tests

Test controller/UI behavior for:

* permission granted
* permission denied
* unavailable API
* timeout
* returned coordinates
* reverse-geocode failure if reverse geocoding is used

Do not require real device GPS for deterministic automated tests.

Real-device permission checks remain part of handoff validation.

---

## Task 3.23 — Offline Tests

Test intentionally with provider/network unavailable.

Verify:

* app shell still loads
* cached weather renders
* stale marker is present
* no cache gives correct empty/offline state
* manual refresh failure preserves cache
* reconnect + successful refresh clears stale state

Where practical, automate controller/storage behavior and manually verify browser/PWA behavior.

---

## Task 3.24 — Documentation

Document durable Phase 3 behavior under `docs/`.

At minimum document:

### Locations

* location model
* deduplication/identity rule
* active-location behavior
* first-run fallback

### Geolocation

* permission flow
* supported/unsupported behavior
* browser/security-context constraints

### Cache

* schema
* cache key/location identity
* write/update behavior
* invalidation/version behavior

### Staleness

* thresholds
* visual semantics
* recovery behavior

### Offline

* expected PWA behavior
* known browser limitations
* testing procedure

---

## Task 3.25 — Durable Decisions

Create decision records only for choices future phases must respect.

Likely candidates:

* location identity/deduplication
* cache schema/versioning
* stale thresholds
* first-run location policy
* online recovery trigger

Do not create ADRs for ordinary implementation details.

---

## Task 3.26 — Pixel / GrapheneOS Validation

Validate on the actual device.

Test:

* manual city search
* saving at least two cities
* switching between them
* restart/relaunch preserving active city
* device-location permission granted
* device-location permission denied
* offline launch with cached data
* stale marker/age
* refresh failure preserving data
* network restoration + successful refresh
* standalone PWA behavior

Record browser used and any permission/PWA quirks.

---

## Task 3.27 — Phase 3 Verification

Verify every Phase 3 acceptance criterion from `PHASES.md`.

At minimum record:

* tests
* lint
* production build
* location search
* saved-location persistence
* active-city persistence
* geolocation granted path
* geolocation denied path
* persistent cache
* offline cached rendering
* stale-state presentation
* failed-refresh preservation
* recovery after reconnect
* reload/PWA relaunch consistency
* earlier-phase regression check

Explicitly confirm that none of the following were implemented:

* radar/maps
* push notifications
* background periodic sync
* severe-weather alert infrastructure
* accounts
* cloud sync
* location history
* background location tracking

---

# Expected Repository Artifacts

By the end of Phase 3, the repository should contain or equivalent:

* normalized location search path
* Cities location-management screen
* saved-location persistence
* active-location persistence
* optional device geolocation
* per-location weather cache
* stale/fresh classification
* offline cached Today behavior
* recovery behavior
* relevant tests
* durable location/cache/offline documentation
* Phase 3 handoff evidence
* decision records only where required

Do not create `tasks/phase-4.md`.

---

# Handoff Notes

Before requesting Phase 3 acceptance, update this file with:

## Files Changed

Created:

* `src/persist.ts` — IndexedDB `org.radilabs.weather` (`places`, `snapshots`, `kv`)
* `src/locations/model.ts`, `src/locations/catalog.ts`
* `src/cache/schema.ts`, `src/cache/stale.ts`, `src/cache/store.ts`
* `src/geo/locate.ts`
* `src/cities/controller.ts`, `src/screens/cities.ts`, `src/styles/cities.css`
* `src/weather/place.ts`, `src/weather/openweather/geocode.ts`
* tests under `tests/locations/`, `tests/cache/`, `tests/geo/`, `tests/cities/`, `tests/weather/geocode.test.ts`
* `tests/cities/bind.test.ts` — Place field stays mounted while typing (query updates do not remount Cities)
* `docs/locations.md`, `docs/geolocation.md`, `docs/weather-cache.md`, `docs/staleness.md`, `docs/offline.md`, `docs/handoffs/phase-3.md`
* `decisions/0012-location-identity.md` … `decisions/0016-online-recovery.md`

Modified:

* Today controller/state/present/screen — cache-first load, generation token, freshness banner, `online` recovery
* Weather provider/client/urls — geocoding
* `src/app.ts`, `src/main.ts`, Settings copy
* Phase 1 scope test — geolocation now allowed; still forbids One Call, `watchPosition`, push, background sync

## Tests Performed

* `npm test` — 19 files, 84 tests (optional live OpenWeather probe needs network)
* `npm run lint`
* `npm run build`
* Optional live OpenWeather probe (Stockholm) when `VITE_OPENWEATHER_API_KEY` is set
* Owner Pixel/GrapheneOS checks remain (see Geolocation / Offline below)

## Results

Automated tests, lint, and production build: pass.

## Location Behavior

* Search: explicit Cities submit → OpenWeather `/geo/1.0/direct` (limit 5), name + region + country
* Saved cities persist in `places` with `order`; survive reload
* Active place is the full `Place` in `kv.active`
* Dedup: `id`/`cacheKey` = `lat.toFixed(4):lon.toFixed(4)`; repeat search/save does not multiply rows
* First-run default: Stockholm SE; not a silent geolocation prompt
* Removing the active saved city falls back to the first remaining saved city, else Stockholm

## Geolocation Validation

* Automated: granted, denied (sticky, no nag), unsupported, timeout, unavailable, insecure, reverse-geocode failure fallback
* Browser/device (owner): Vanadium / GrapheneOS Pixel — permission granted, permission denied, standalone PWA, insecure LAN preview if used
* Limitations: needs secure context; no `watchPosition`; permission never on startup

## Cache and Staleness

* Schema version `1`, provider `openweather`, key = location `cacheKey`
* Fresh: age under 30 minutes (`CACHED`); ≥ 30 minutes `STALE`; live fetch clears badges
* Age copy: `UPDATED 47 MIN AGO` style

## Offline / Recovery Validation

* Automated: cache-first Today, failed refresh preserves cache, city A in-flight does not paint as city B, `onOnline` + successful refresh → `live`
* Owner: airplane-mode PWA relaunch, stale marker, refresh failure, restore network (see `docs/offline.md`)

## Known Limitations

* Dev server is not the offline PWA; use preview/install
* `http://192.168.x.x` is usually an insecure context for geolocation
* Firefox localhost vs LAN IP are different origins
* Reverse geocode can fail; label becomes `Device location`
* No radar, alerts, polling, accounts, or cloud sync

## Deferred Work

* Display-unit settings and provider configuration UI
* Radar/maps (Phase 4)
* Push / severe-weather alerts
* Background periodic sync
* Cloud sync / accounts / location history
* Auto-save of device location (intentionally not done; GPS jitter)

## Decisions Created

* `0012` location identity / deduplication
* `0013` weather cache schema / versioning
* `0014` stale thresholds
* `0015` first-run location policy
* `0016` online recovery trigger

---

# Completion Rule

PWA Phase 3 is stopped/superseded. It is not accepted.

Do not complete this PWA phase. Do not begin PWA Phase 4. Do not begin native implementation until Native Phase 0 is explicitly authorized.
