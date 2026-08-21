# Phase 1 — Weather Data

## Status

**Accepted** — 2026-08-21 (PWA prototype).

This file is historical PWA evidence. Native work is not this phase. Do not execute this file.

Owner verified live Stockholm retrieval (current, 40 forecast points, 6 local days with partial first/last, OpenWeather AQI 2 / fair).

Do not begin Phase 2 until that phase is explicitly authorized.

---

## Objective

Connect Weather to the **OpenWeather free APIs** and establish a stable application-owned weather data model.

The primary outcome is:

**provider data → validated provider boundary → normalized Weather models**

Phase 1 is about trustworthy data, not final UI.

Do not redesign the Today screen.

---

## Phase Constraints

Phase 1 must respect the existing Phase 0 application structure and design system.

Do not implement:

* final Today screen integration/polish
* geolocation
* city search
* saved cities
* offline weather caching
* radar/maps
* background refresh
* notifications
* paid OpenWeather functionality
* historical weather
* 16-day forecast
* generic provider/plugin architecture

Use the OpenWeather **free-plan capabilities only**.

---

## Task 1.1 — Inspect Current Foundation

Done.

Phase 0 assumptions hold (Vite vanilla TS, IndexedDB proof DB separate, PWA shell). `TASKS.md` was stale (still “Phase 1 not authorized”); updated to match this authorized file. No Phase 0 decisions reopened.

---

* read `PROJECT.md`
* read `PHASES.md`
* read `TASKS.md`
* read `tasks/README.md`
* read this file
* inspect Phase 0 decisions and relevant docs
* inspect existing application/storage/test structure

Confirm that Phase 0 assumptions still hold.

Do not reopen accepted Phase 0 decisions without a genuine contradiction.

Record any unrelated discoveries under **Deferred Work**.

---

## Task 1.2 — Define Normalized Weather Models

Create application-owned TypeScript models independent of OpenWeather payloads.

At minimum define models for:

### Location

Include enough information for weather data to identify:

* display name where known
* latitude
* longitude
* country/region where available
* timezone information where required for forecast presentation

Do not implement location search yet.

### Current conditions

Include at minimum:

* observation timestamp
* temperature
* feels-like
* daily/current high where supplied
* daily/current low where supplied
* condition identifier
* human-readable condition
* weather glyph/category identifier
* visibility
* cloud coverage where useful

### Wind

Include:

* speed
* direction
* gust where supplied

### Precipitation

Support the provider data that may exist for:

* rain
* snow
* probability
* measured amount

Missing values must remain distinguishable from real zero values.

### Atmospheric data

Include:

* humidity
* pressure
* air-quality data where available
* UV only if actually available through the selected free endpoint

Do not manufacture unsupported data.

### Forecast point

Represent each provider forecast point with enough information for later UI to show:

* timestamp
* temperature
* feels-like where useful
* condition
* precipitation probability
* rain/snow amount where supplied
* wind
* humidity
* pressure

### Daily forecast summary

Define an application-owned daily summary suitable for Phase 2.

Include at minimum:

* local date
* high
* low
* representative condition
* precipitation probability/summary
* source forecast points or enough traceability to understand how the summary was derived

Do not hard-code exactly seven days.

---

## Task 1.3 — Define Canonical Internal Units

Choose and document the units used inside normalized Weather models.

The internal representation must be consistent regardless of future user display preferences.

Define canonical units for at minimum:

* temperature
* wind speed
* pressure
* precipitation
* visibility
* percentages
* timestamps

Future display-unit conversion belongs above the provider layer.

Do not build the Settings units UI in Phase 1.

If this becomes a durable compatibility rule, record it under `decisions/`.

---

## Task 1.4 — Implement the Provider Boundary

Create the smallest useful provider abstraction around OpenWeather.

The application should call an application-owned interface/service rather than OpenWeather-specific functions from UI code.

The boundary should expose operations equivalent to:

* get current weather for coordinates
* get forecast for coordinates
* get air-quality data for coordinates where supported

Avoid building:

* provider registration
* provider discovery
* dynamic plugins
* capability negotiation frameworks
* multiple-provider configuration

There is one provider now: OpenWeather.

Replaceability means clean boundaries, not architecture cosplay.

---

## Task 1.5 — Implement OpenWeather Client

Implement the OpenWeather free API client.

Requirements:

* use HTTPS
* centralize base URLs
* centralize request construction
* centralize credential handling
* support request cancellation/timeout where practical
* validate HTTP status before parsing
* parse JSON defensively
* never expose the API key through application logs or error messages

Use coordinates for weather retrieval.

For development/integration testing, a fixed known location such as **Stockholm** may be used.

Do not implement browser geolocation or city search.

---

## Task 1.6 — Resolve Credential Strategy

Determine the v1 OpenWeather API-key strategy.

Explicitly evaluate the reality that browser-delivered JavaScript cannot securely hide a bundled reusable API key.

Choose the smallest strategy appropriate for this project's intended personal/static PWA deployment.

Document:

* where the key is supplied
* where it exists at build/runtime
* whether it is visible to a browser user
* what must never be committed to Git
* local development setup
* production/deployment setup
* rotation procedure
* limitations of the chosen approach

Add appropriate `.gitignore` rules where required.

Provide an example configuration file only with placeholders.

Never commit the actual API key.

If future work must respect the chosen strategy, create a decision record.

Do not introduce a backend solely to pretend a browser secret is secret unless the Phase 1 contract genuinely requires one.

---

## Task 1.7 — Normalize Current Weather

Transform the OpenWeather current-weather response into the application-owned current-weather model.

Requirements:

* no raw OpenWeather response object leaves the provider implementation
* optional/missing fields are handled explicitly
* provider weather codes are converted into stable application condition categories
* timestamps are preserved correctly
* numeric units match the canonical internal-unit decision

Do not let UI code interpret OpenWeather weather codes.

---

## Task 1.8 — Normalize Forecast Points

Transform the free forecast response into normalized forecast points.

Preserve enough provider information to support later deterministic daily aggregation without leaking OpenWeather schema into the rest of the application.

Handle:

* temperature
* weather condition
* precipitation probability
* rain/snow amounts
* wind
* humidity
* pressure
* forecast timestamp

Malformed individual entries must not silently corrupt the full forecast.

Define and test the intended failure behavior.

---

## Task 1.9 — Build Daily Forecast Aggregation

Create deterministic logic that converts forecast points into daily summaries.

The algorithm must explicitly define:

* which timezone determines calendar-day boundaries
* how daily high is calculated
* how daily low is calculated
* how representative weather condition is selected
* how precipitation probability is summarized
* how incomplete first/last days are represented

Do not pretend partial forecast days are complete observations.

The transformation must work for whatever forecast horizon the free API actually returns.

Do not assume seven days.

---

## Task 1.10 — Weather Condition Mapping

Create an application-owned weather-condition vocabulary.

It should be detailed enough to support the existing/future technical glyph system without mirroring every provider-specific code.

Examples of useful categories may include:

* clear
* partly cloudy
* cloudy
* overcast
* light rain
* rain
* heavy rain
* thunderstorm
* light snow
* snow
* fog/mist

Exact categories are an implementation decision.

Requirements:

* mapping from OpenWeather codes is deterministic
* unknown future provider codes have a safe fallback
* UI code consumes application categories, not OpenWeather numeric codes
* mapping is unit tested

Do not build the complete Phase 2 glyph library.

---

## Task 1.11 — Air Quality

Integrate OpenWeather free air-quality data if available under the active plan.

Normalize useful data into an application-owned model.

At minimum preserve:

* observation timestamp
* provider AQI category/index
* useful pollutant values where supplied

Document what OpenWeather's AQI value means rather than presenting it as a universally standardized AQI number if it is not one.

If air-quality retrieval is unavailable under the actual free account, document the limitation and ensure the rest of Phase 1 remains functional.

Do not introduce a second provider merely to fill the gap.

---

## Task 1.12 — Application Error Model

Define stable application-level errors/states for provider operations.

At minimum distinguish:

* authentication failure
* rate limiting
* location/not-found response where applicable
* network unavailable
* timeout
* provider/server failure
* malformed/unexpected provider response
* unknown failure

Provider-specific error bodies must not leak directly into normal UI code.

Errors should preserve useful diagnostic context for development without leaking secrets.

---

## Task 1.13 — API Integration Proof

Provide a reproducible way to demonstrate real weather retrieval during Phase 1.

The proof must retrieve:

* current weather
* forecast
* air quality where available

for a known coordinate.

The proof may be:

* a small development-only diagnostic
* a test/integration command
* another minimal mechanism fitting the existing application

Do not turn this into the Phase 2 Today UI.

The static Phase 0 Today screen may remain static during this phase.

---

## Task 1.14 — Provider Fixtures

Capture sanitized representative provider fixtures for deterministic tests.

Fixtures must:

* contain no API credentials
* represent realistic current-weather data
* represent realistic forecast data
* represent air-quality data where used
* include important optional/missing-field cases where useful

Tests should not require spending live API calls for every run.

Live API checks and deterministic unit tests are separate things.

---

## Task 1.15 — Transformation Tests

Add automated tests for at minimum:

* current-weather normalization
* forecast-point normalization
* unit normalization
* condition mapping
* daily high calculation
* daily low calculation
* representative-condition selection
* precipitation summary
* timezone/day boundary handling
* incomplete-day behavior
* optional/missing precipitation values
* malformed payload handling

Tests must exercise application-owned models, not merely snapshot raw OpenWeather JSON.

---

## Task 1.16 — Error Tests

Test provider/error handling for at minimum:

* HTTP 401/authentication failure
* HTTP 429/rate limit
* server error
* request timeout
* network failure
* malformed JSON or structurally invalid response

Ensure errors become the expected application-level states.

---

## Task 1.17 — Request Discipline

Ensure the implementation does not make wasteful provider calls.

For Phase 1:

* requests happen explicitly
* no polling loop
* no background refresh
* no automatic repeated retry storm
* no call per rendered component
* related UI consumers must ultimately be able to share one normalized result

Caching weather data belongs to Phase 3.

A short-lived in-memory result used to prevent duplicate requests during one operation is acceptable if necessary.

Do not implement persistent weather caching.

---

## Task 1.18 — Documentation

Add durable provider documentation under `docs/`.

Document at minimum:

### OpenWeather integration

* endpoints used
* free-plan assumptions
* expected forecast horizon/resolution
* provider request parameters
* relevant rate limits
* fields relied upon

### Normalized schema

Document:

* major application-owned weather models
* canonical units
* condition vocabulary
* optional-field behavior

### Forecast aggregation

Document:

* timezone rule
* daily high/low algorithm
* representative-condition rule
* precipitation aggregation
* incomplete-day behavior

### Credentials

Document the chosen API-key workflow separately and clearly.

Future implementation agents should not need to reverse-engineer Phase 1 code to understand these rules.

---

## Task 1.19 — Durable Decisions

Create decision records only for choices future phases must respect.

Likely candidates include:

* OpenWeather credential strategy
* canonical internal units
* normalized weather model boundary
* daily aggregation semantics

Do not create ADRs merely because individual source files were written.

---

## Task 1.20 — Phase 1 Verification

Perform a complete Phase 1 verification pass.

Verify every Phase 1 acceptance criterion in `PHASES.md`.

At minimum record:

* install/dependency result
* lint result
* automated test result
* production build result
* successful real current-weather request
* successful real forecast request
* successful air-quality request or documented free-plan limitation
* normalized output inspection
* authentication-error test
* rate-limit behavior test or deterministic simulation
* malformed-response test
* timeout/network-error test

Confirm explicitly that none of the following were implemented:

* geolocation
* saved cities
* city search
* persistent weather caching
* radar/maps
* background refresh
* notifications
* paid API endpoints
* historical weather
* final Phase 2 Today redesign

---

# Expected Repository Artifacts

By the end of Phase 1, the repository should contain or equivalent:

* application-owned weather model definitions
* OpenWeather provider implementation
* provider boundary/interface
* weather-condition mapping
* forecast aggregation logic
* application-level error model
* deterministic provider fixtures
* transformation/unit tests
* provider/error tests
* documented credential workflow
* OpenWeather integration documentation
* normalized-model documentation
* decision records where required
* Phase 1 handoff evidence

Do not create `tasks/phase-2.md`.

---

# Handoff Notes

## Files Changed

* `src/weather/**` models, errors, conditions, daily aggregation, provider, OpenWeather client/normalize
* `src/screens/settings.ts` — Stockholm weather probe (Today remains static)
* `src/vite-env.d.ts` — `VITE_OPENWEATHER_API_KEY`
* `tests/weather/**`, `tests/fixtures/openweather/**`, `tests/scope.test.ts`
* `.env.example`, `vitest.config.ts`, `tsconfig.json`, `TASKS.md`
* `docs/openweather.md`, `docs/weather-models.md`, `docs/forecast-aggregation.md`, `docs/credentials.md`, `docs/development.md`, `docs/handoffs/phase-1.md`
* `decisions/0007`–`0010`

## Tests Performed

```sh
npm test     # 11 files, 40 tests, passed (including live when key present)
npm run lint # passed
```

Owner live probe (2026-08-21, Firefox): Settings → Fetch Stockholm weather.

Reported: Stockholm SE, 18.11°C, overcast clouds, 40 points, 6 days (first/last partial), AQ fair (OW 2).

## Results

Automated transformation, aggregation, error, and scope tests: **pass**.

Live retrieval: **pass** (owner, 2026-08-21).

Phase 0 regression: Today remains static `fake-today.ts`.

Phase 1: **accepted**.

## Provider Endpoints Used

* `https://api.openweathermap.org/data/2.5/weather`
* `https://api.openweathermap.org/data/2.5/forecast`
* `https://api.openweathermap.org/data/2.5/air_pollution`

## Credential Strategy

`VITE_OPENWEATHER_API_KEY` via `.env` / build env. Inlined into the client. Never committed. See `docs/credentials.md` and `decisions/0008-openweather-credentials.md`.

## Known Limitations

* Free forecast is 3-hour / ~5 days, not hourly and not 16-day.
* UV is not available on these free endpoints.
* Current `temp_min`/`temp_max` are not true daily range.
* OpenWeather AQI 1–5 is not EPA/CAQI.
* No IANA timezone name; offset seconds only.
* Browser-delivered API key is extractable.
* Air quality is optional on snapshot if that call fails (except auth).
* Condition vocabulary is wider than Phase 0 glyphs (snow/thunder/fog deferred to Phase 2 glyphs).
* Firefox requires `fetch` to be invoked as a window method; a detached `fetch` reference throws TypeError.
* Dev/preview uses a same-origin `/ow` Vite proxy; production static hosts still call OpenWeather directly (CORS/Firefox behavior may differ).

## Deferred Work

* Bind Today to live snapshot — Phase 2
* Geolocation, city search, saved cities — Phase 3
* Persistent weather cache — Phase 3
* Radar — Phase 4
* Display-unit settings — later
* Backend proxy if the owner wants the key off the client
* Extra glyphs for snow/thunder/fog
* History API vs hash routing

## Decisions Created

* `decisions/0007-canonical-internal-units.md`
* `decisions/0008-openweather-credentials.md`
* `decisions/0009-provider-boundary.md`
* `decisions/0010-daily-aggregation.md`

---

# Completion Rule

Phase 1 is complete only when the Phase 1 acceptance criteria and handoff contract in `PHASES.md` are satisfied.

Phase 1 is accepted.

**STOP. Do not begin Phase 2 until it is explicitly authorized.**
