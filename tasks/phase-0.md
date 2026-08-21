# Native Phase 0 — Android Foundation

## Status

**Accepted** — 2026-08-21.

Owner validated on Pixel / GrapheneOS:

* APK installs
* app launches independently
* Today visual direction accepted
* navigation works
* API key save works
* configured state survives restart
* API key removal works

Do not begin Native Phase 1 until that phase is explicitly authorized.

---

## Objective

Create the smallest working native Weather application and establish build, install, and debugging workflow.

The application must already establish the intended visual direction without implementing real weather functionality.

The primary outcome is:

**Kotlin + Compose APK + graphite Today shell + local persistence + runtime API-key storage**

Derived from the immutable contract in `PHASES.md` (Native Phase 0). That contract is not modified here.

---

## Phase Constraints

* Application ID: `org.radilabs.weather` (`decisions/0001-application-identity.md`)
* App name: `Weather`
* Version: `0.0.0` (versionCode `1`)
* Kotlin + Jetpack Compose
* Single `app` module
* GrapheneOS / Pixel first
* No WebView, Capacitor, TWA, Flutter, RN, KMP, Hilt
* No OpenWeather network calls

---

## Task 0.1 — Inspect contract

Read `PROJECT.md`, `PHASES.md`, `TASKS.md`, `tasks/README.md`, `decisions/0017`, identity/design/credential docs.

Confirm no blocking contradiction with Native Phase 0.

PWA task files live under `tasks/pwa/` so this file can occupy `tasks/phase-0.md` without deleting history.

---

## Task 0.2 — Android project

Create a minimal Gradle Android project:

* Kotlin
* Jetpack Compose
* current stable AGP compatible with a user-local JDK 21
* debug and release APK tasks
* release APK uses the debug keystore until a production keystore exists (document this)

---

## Task 0.3 — Identity, icons, shell

* Package `org.radilabs.weather`
* Bottom navigation: Today, Radar, Cities, Settings
* Radar and Cities are placeholders
* Settings holds API-key configuration
* Today is the only real screen

---

## Task 0.4 — Design tokens + static Today

Reproduce `docs/design-system.md` in Compose.

Static Stockholm sample data (no network, no fake async provider).

Show: location, temperature, condition, high/low, feels-like, 3-hour strip, multi-day rows with range bar, wind, precipitation, humidity, pressure, visibility, timestamp/metadata.

---

## Task 0.5 — Persistence + API key

App-private local storage for the OpenWeather key.

Settings: enter, save, replace, remove, masked configured state.

Do not log the key. Do not bundle it. Do not use BuildConfig or Gradle properties.

Persistence must survive process restart.

Do not implement weather cache or saved cities.

---

## Task 0.6 — Tests

Useful tests only:

* API-key save / replace / remove / configured state
* persistence via the store abstraction
* no credential strings in source

---

## Task 0.7 — Docs, decisions, handoff

* `docs/development.md` — build/install/adb
* `docs/android-project.md` — structure
* `docs/design-system.md` — Compose token mapping
* `docs/credentials.md` — storage choice and honesty
* Decision only if future phases must respect a storage/toolchain choice
* `docs/handoffs/phase-0.md` — implemented / awaiting owner acceptance

---

## Explicit Exclusions

Same as `PHASES.md` Phase 0 plus: real forecasts, geolocation, city search, saved cities, weather cache, radar/maps, notifications, widgets, WorkManager, accounts, analytics, AI, cloud sync.

---

## Files Changed

Created:

* `app/` Kotlin + Compose Weather application and unit tests
* Gradle wrapper (`gradlew`, `gradle/wrapper/`)
* `decisions/0018-runtime-api-key-storage.md`
* `docs/android-project.md`
* `docs/handoffs/phase-0.md` — accepted 2026-08-21

Modified:

* `TASKS.md`, `tasks/README.md`, `README.md`, `docs/development.md`, `docs/credentials.md`, `docs/design-system.md`

## Tests Performed

* `./gradlew :app:testDebugUnitTest` — pass (`ApiKeyStore`, `Dest`)
* `./gradlew :app:assembleDebug` — `app/build/outputs/apk/debug/app-debug.apk`
* `./gradlew :app:assembleRelease` — `app/build/outputs/apk/release/app-release.apk` (debug-signed)
* APK permission dump: no `INTERNET`
* APK string dump: no bundled OpenWeather key
* Confirmed no `package.json` / PWA `src/` / `tasks/phase-1.md`

## Results

Automated unit tests and both APK build paths: pass.

Owner device checks: **pass** (Pixel / GrapheneOS, 2026-08-21).

---

## Known Limitations

* User-local JDK 21 + Android SDK; `local.properties` is gitignored
* Release APK is debug-signed
* APK size is large without minify
* No emulator/device in this implementation environment

## Deferred Work

* Production signing keystore
* Native Phase 1 OpenWeather integration
* Radar, cities, cache, location (later native phases)

## Decisions Created

* `0018` runtime API-key storage (app-private SharedPreferences)

---

# Completion Rule

Native Phase 0 is accepted.

**STOP. Do not begin Phase 1 until it is explicitly authorized. Do not create `tasks/phase-1.md`.**
