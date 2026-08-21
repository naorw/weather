# Weather — Phase Contracts

These phases define immutable execution boundaries for the **native Android** product.

Tasks inside a phase may be refined as implementation progresses, but the phase goal, scope, exclusions, acceptance criteria, and handoff contract must not be changed during implementation.

Work discovered outside the active phase is recorded as deferred work and is not implemented.

A completed task does not mean a completed phase.

The implementation agent must stop after satisfying the active phase handoff contract.

Detailed task files are created only for the currently authorized phase.

The PWA prototype roadmap and its accepted/stopped work live in Git history and in historical files under `tasks/` and `docs/handoffs/`. Those are **not** phases of the native implementation. Native work starts again at Phase 0.

---

# Phase Map

| Phase | Name                  | Outcome                                                                                         |
| ----- | --------------------- | ----------------------------------------------------------------------------------------------- |
| 0     | Android Foundation    | Installable Kotlin/Compose APK with design-system baseline, navigation shell, and local proofs  |
| 1     | Weather Data          | OpenWeather free APIs work through normalized internal models                                   |
| 2     | Weather Instrument    | Today screen implements the core retro-technical design language                                |
| 3     | Locations and Offline | Saved cities, geolocation, caching, stale-state handling, and recovery work reliably            |
| 4     | Radar and Maps        | Useful technical map/radar view exists within the free-first constraint                         |
| 5     | Daily-Use Polish      | GrapheneOS/Pixel install, performance, accessibility, reliability, and production polish        |

Future capabilities require additional phase contracts.

---

# Phase 0 — Android Foundation

## Goal

Create the smallest working native Weather application and establish the development, build, install, and debugging workflow.

The application must already establish the intended visual direction without implementing real weather functionality.

## Entry Conditions

* Repository exists.
* `PROJECT.md` exists.
* Native Android platform decision `0017` is accepted.
* An Android development environment is available.
* Owner/planner has **explicitly authorized** this phase and created `tasks/phase-0.md`.
* No later-phase functionality is required.

## Scope

* Create a minimal Android application project.
* Use Kotlin and Jetpack Compose.
* Establish application identity (`org.radilabs.weather` unless the owner changes `0001`).
* Establish debug install on a device or emulator and a production/release APK build path.
* Document build, run, install, and debugging workflow for GrapheneOS / Pixel.
* Create the main navigation shell:

  * Today
  * Radar
  * Cities
  * Settings
* Create a static Today screen reproducing the accepted design language:

  * current condition hero
  * hourly strip
  * multi-day forecast
  * atmospheric data area
* Establish initial design tokens in Compose (graphite palette, typography hierarchy, accents, spacing, borders, shape).
* Prove local persistence with a small read/write of structured data (not weather cache).
* Prove runtime local storage/configuration of an OpenWeather API key (no network call yet).
* Do not wrap the old PWA in WebView, Capacitor, or a Trusted Web Activity.

## Explicit Exclusions

* Real OpenWeather calls.
* Geolocation.
* City search.
* Saved cities.
* Real forecasts.
* Offline weather caching.
* Radar/maps.
* Background refresh.
* Notifications.
* Widgets.
* WorkManager periodic work.
* AI features.
* Analytics.
* User accounts.

## Acceptance Criteria

1. Application builds.
2. A debug APK installs and launches on Android.
3. A release/production APK can be generated.
4. Bottom navigation works between placeholder screens.
5. Today screen clearly demonstrates the intended visual direction.
6. UI remains usable at common Pixel portrait sizes.
7. Local structured storage can be initialized and accessed.
8. An API key can be stored and read back locally without a network request.
9. Build/run/debug/install documentation exists.
10. No real weather-provider integration exists.
11. No later-phase functionality has been implemented.

## Handoff Contract

Before Phase 0 can be declared complete:

* All acceptance criteria must be demonstrated.
* Build and install workflow must be documented.
* Design tokens and core layout rules must be documented if future phases depend on them.
* Files changed must be recorded.
* Tests performed and results must be recorded.
* Known limitations must be recorded.
* Deferred discoveries must be recorded.
* Architectural decisions that future work must respect must be recorded under `decisions/`.

Then STOP.

Do not begin Phase 1.

---

# Phase 1 — Weather Data

## Goal

Connect Weather to the OpenWeather free APIs and establish stable application-owned weather models.

The UI may still be visually incomplete.

The important outcome is trustworthy data acquisition and normalization.

## Entry Conditions

* Native Phase 0 handoff contract is satisfied.
* Application shell works.
* Local storage works.
* Runtime credential storage works.
* Build and APK workflow works.

## Scope

### Provider boundary

Create a small internal provider boundary.

OpenWeather response objects must not leak throughout the UI.

Define application-owned normalized models for at minimum:

* location
* current conditions
* hourly/3-hour forecast points
* multi-day forecast
* wind
* humidity
* pressure
* precipitation
* visibility
* air quality where available

Do not build a generic plugin architecture.

### OpenWeather integration

Implement required free API calls using the locally stored key.

Support enough data to power:

* current weather
* immediate forecast
* available multi-day forecast
* atmospheric detail values
* air-quality data where practical within the free APIs

### Forecast transformation

OpenWeather's free forecast format may not directly match the application's presentation model.

Implement deterministic transformation logic for:

* hourly/3-hour presentation
* daily high/low aggregation
* precipitation probability
* weather-condition summaries

Transformation behavior must be testable.

### Errors

Handle:

* invalid credentials
* rate limiting
* location not found
* malformed provider data
* provider timeout
* temporary connectivity failure

Errors must produce application-level states rather than raw provider errors leaking directly into UI components.

### Credential use

Read the key from local runtime storage. Do not embed it in source or the APK.

Document what is and is not secret on a personal device.

## Explicit Exclusions

* Final Today screen design.
* Geolocation.
* Saved cities.
* Offline weather cache behavior.
* Radar/maps.
* Background refresh.
* Notifications.
* Paid OpenWeather APIs.
* Historical weather.
* 16-day forecast.
* Generic weather-provider plugin system.

## Acceptance Criteria

1. Current weather can be retrieved successfully.
2. Forecast data can be retrieved successfully.
3. Provider responses are converted into application-owned models.
4. UI components do not depend directly on raw OpenWeather payload structure.
5. Daily summaries/high-low values are generated deterministically.
6. Provider failures become stable application error states.
7. Rate-limit and authentication failures are distinguishable.
8. API credential handling is documented.
9. Unit tests exist for important transformation logic.
10. Phase 0 functionality does not regress.

## Handoff Contract

Before Phase 1 can be declared complete:

* Normalized weather models must be documented.
* OpenWeather endpoints used must be documented.
* Transformation behavior must be tested.
* Error behavior must be tested.
* Credential strategy must be documented.
* Known provider/free-plan limitations must be recorded.
* Deferred work must be recorded.
* Decisions affecting later phases must be recorded.

Then STOP.

Do not begin Phase 2.

---

# Phase 2 — Weather Instrument

## Goal

Turn the Today screen into the actual product.

The result must implement the project's defined retro-technical weather-instrument design language while presenting real data clearly.

## Entry Conditions

* Phase 1 handoff contract is satisfied.
* Real normalized weather data is available.
* Provider failures are represented cleanly.

## Scope

### Hero / current conditions

Implement the dominant current-weather region.

Display:

* location
* current temperature
* condition
* high / low
* feels-like
* selected technical metadata
* data timestamp

The visual hierarchy must prioritize current conditions above all other information.

### Hourly forecast

Implement the immediate forecast strip.

It must be:

* horizontally scannable
* touch-friendly
* visually compact
* clearly separated from current conditions

### Multi-day forecast

Implement the available free-plan forecast horizon.

Display at minimum:

* day
* condition
* precipitation probability where available
* low
* high
* visual temperature-range representation

The UI must not hard-code an assumption of seven days.

### Atmospheric detail

Implement useful technical modules for:

* wind
* precipitation
* humidity
* pressure
* visibility
* air quality or UV where data exists

Avoid filling the screen with equally weighted cards.

Detailed data belongs below the core forecast hierarchy.

### Design system

Apply the established visual language consistently:

* graphite/charcoal surfaces
* restrained cyan/teal
* restrained amber
* technical typography hierarchy
* subtle grid/divider language
* functional weather glyphs
* minimal blur
* minimal decorative effects

### Interaction

Support basic detail interaction where needed without introducing complex navigation.

## Explicit Exclusions

* Location permission.
* Saved city management.
* Offline weather caching.
* Radar/maps.
* Notifications.
* Weather alerts.
* AI commentary.
* Animation systems for their own sake.
* Paid data.

## Acceptance Criteria

1. Today screen uses real weather data.
2. Current conditions are visually dominant.
3. Hourly forecast is easy to scan on a phone.
4. Multi-day forecast works with the actual available forecast horizon.
5. Atmospheric details are readable without overwhelming the page.
6. Visual design clearly matches the project design direction.
7. UI does not resemble a generic Material card dashboard.
8. Text remains readable at common Android font scaling levels.
9. Touch targets are usable.
10. Loading, empty, and provider-error states fit the same visual system.
11. Phase 1 data behavior does not regress.

## Handoff Contract

Before Phase 2 can be declared complete:

* Today screen must be visually reviewed on a real Pixel / GrapheneOS device.
* Loading/error/empty states must be demonstrated.
* Design tokens/components future screens depend on must be documented.
* Accessibility problems discovered must be recorded.
* Tests and results must be recorded.
* Known limitations must be recorded.
* Deferred work must be recorded.

Then STOP.

Do not begin Phase 3.

---

# Phase 3 — Locations and Offline

## Goal

Make Weather reliable for normal daily use.

Users must be able to choose locations, save them, use optional device location, and still see useful recent weather during temporary network loss.

## Entry Conditions

* Phase 2 handoff contract is satisfied.
* Today screen works with real provider data.
* Core visual system is stable.

## Scope

### Manual location search

Support searching for locations using provider/geocoding capabilities.

Search results must provide enough context to distinguish similarly named places.

### Saved cities

Support:

* save city
* remove city
* switch city
* persist city order where practical
* remember last active city

### Device location

Support optional Android location permission.

Requirements:

* explicit user action
* graceful denial
* no requirement to grant location permission
* no background location tracking
* no silent location access on startup

### Weather cache

Persist latest successful weather data per saved/current location.

Store enough metadata to determine:

* retrieval time
* location
* provider
* age/staleness

Never show one city's cached or in-flight result under another city's name.

Never erase a valid cached snapshot because refresh failed.

Cached data must never pretend to be live.

### Offline behavior

When the network is unavailable:

* the application must still launch
* last successful data should display where available
* stale data must be visually identified
* failed refresh must not erase valid cached data

### Recovery

When connectivity returns:

* refresh should work cleanly without polling
* stale indicators should clear only after successful new data
* duplicate or corrupt location state must not be created

## Explicit Exclusions

* Radar/maps.
* Push notifications.
* Background periodic sync.
* Severe-weather alert infrastructure.
* Accounts.
* Cloud sync.
* Location history.
* Background location tracking.

## Acceptance Criteria

1. Location search works.
2. Saved cities persist across process death and reboot.
3. Active city persists.
4. Device location works when permission is granted.
5. Application remains fully usable when permission is denied.
6. Latest successful weather data persists locally.
7. Cached data displays offline.
8. Cached data is clearly marked stale with an understandable age/state.
9. Failed refresh does not destroy cached weather.
10. Recovery after restored connectivity works.
11. Location state remains consistent across restart.
12. Earlier phase behavior does not regress.

## Handoff Contract

Before Phase 3 can be declared complete:

* Offline behavior must be tested intentionally with network disabled.
* Permission-granted and permission-denied paths must be tested on Pixel / GrapheneOS.
* Cache schema and invalidation rules must be documented.
* Staleness rules must be documented.
* Tests and results must be recorded.
* Known platform limitations must be recorded.
* Deferred work must be recorded.

Then STOP.

Do not begin Phase 4.

---

# Phase 4 — Radar and Maps

## Goal

Add a useful map-oriented meteorological view without compromising the free-first and privacy-conscious product constraints.

The Radar screen should feel like a technical weather instrument, not a generic embedded map.

## Entry Conditions

* Phase 3 handoff contract is satisfied.
* Location system works.
* Core design system is stable.
* Free-data constraints are understood.

## Scope

### Map foundation

Implement a native map view appropriate for GrapheneOS / Pixel.

Support:

* current location/city focus
* pan
* zoom
* clear current-location marker
* dark technical visual treatment

### Weather overlay

Use weather/map data available under the chosen free-data strategy.

Possible useful layers include:

* precipitation
* clouds
* temperature
* wind
* pressure

Only implement layers actually supported reliably by the selected free source.

### Layer controls

Provide simple controls for available layers.

Do not expose unavailable or misleading controls.

### Visual language

Radar must lean further into the project's technical design vocabulary:

* grid
* contours
* vector/field representation where meaningful
* restrained technical legend
* minimal decorative chrome

### Failure handling

Map/radar failure must not affect the Today screen.

## Explicit Exclusions

* Paid radar products.
* Historical radar.
* Weather animation requiring paid data unless explicitly free and reliable.
* Route weather.
* Aviation/marine professional tooling.
* Generic GIS features.
* Drawing/annotation tools.
* AI explanations.

## Acceptance Criteria

1. Radar screen opens and renders correctly on Pixel portrait sizes.
2. Current selected location can be shown on the map.
3. At least one genuinely useful weather layer works.
4. Layer controls accurately reflect available data.
5. Map style matches the rest of the application.
6. Map remains usable with touch gestures.
7. Weather-layer failure degrades gracefully.
8. Today and Cities screens are unaffected by map failures.
9. No paid API dependency has been introduced unless the project contract was explicitly changed before this phase began.

## Handoff Contract

Before Phase 4 can be declared complete:

* Data source and licensing/usage constraints must be documented.
* Supported layers must be documented.
* Mobile performance must be tested.
* Failure/offline behavior must be tested.
* Known limitations must be recorded.
* Deferred work must be recorded.

Then STOP.

Do not begin Phase 5.

---

# Phase 5 — Daily-Use Polish

## Goal

Make Weather a dependable v0.1 daily-use application on GrapheneOS / Pixel.

This phase improves reliability and finish.

It does not expand product scope.

## Entry Conditions

* Phase 4 handoff contract is satisfied.
* Core product capabilities work end-to-end.

## Scope

### Android packaging

Verify and polish:

* application name
* icon set
* splash / launch
* version visibility
* debug vs release APK
* install on GrapheneOS / Pixel

### Performance

Improve:

* cold start
* cached startup
* API refresh responsiveness
* map loading
* unnecessary network calls

### Accessibility

Review:

* font scaling
* contrast
* TalkBack labels
* touch targets
* reduced-motion preference where motion exists

### Reliability

Handle cleanly:

* application update
* corrupted cached data
* missing storage
* denied permissions
* provider downtime
* API-rate-limit state

### UX polish

Improve:

* loading transitions
* refresh behavior
* city switching
* settings clarity
* empty states
* subtle motion where it improves comprehension

### Documentation

Complete:

* local development
* build
* APK install
* API configuration
* troubleshooting
* known limitations

## Explicit Exclusions

* New major screens.
* New paid weather features.
* Historical weather.
* 16-day forecast.
* AI.
* Accounts.
* Cloud sync.
* Widgets.
* Watch-face integration.
* Notification infrastructure.
* WorkManager periodic sync.
* Generic provider plugin architecture.
* PWA or hybrid rewrite.

## Acceptance Criteria

1. Application can be installed and launched reliably as an APK on Pixel / GrapheneOS.
2. Upgrade/update behavior works without breaking local state.
3. Today, Cities, Settings, and Radar work together without major regressions.
4. Offline startup remains useful.
5. Normal provider outages are understandable to the user.
6. Accessibility basics are validated.
7. No obvious high-frequency redundant API requests occur.
8. UI remains visually coherent across all screens.
9. Deployment instructions are complete.
10. Known limitations are documented.
11. Version is visible somewhere appropriate.
12. Product is suitable for personal daily use as v0.1.

## Handoff Contract

Before Phase 5 can be declared complete:

* Full regression pass must be performed.
* Installation must be tested on a real Pixel / GrapheneOS device.
* Offline/recovery flow must be retested.
* Accessibility review must be recorded.
* Performance observations must be recorded.
* Deployment documentation must be complete.
* All known limitations and deferred work must be recorded.
* v0.1 release state must be explicitly declared.

Then STOP.

Future work requires a new phase contract.
