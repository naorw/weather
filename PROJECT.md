# Weather

## General Information

**Weather** is a small native Android weather instrument, designed primarily for **GrapheneOS / Pixel**.

The goal is not to build another generic weather dashboard.

The goal is to build a fast, visually distinctive weather instrument that presents useful weather information clearly, installs as a real Android application (APK), and remains useful even when connectivity is temporarily unavailable.

Initial weather data comes from the **OpenWeather free APIs**.

The weather provider must remain replaceable.

A completed PWA prototype exists in Git history. It is not the v1 product platform. See `decisions/0017-native-android-platform.md`.

---

## Core Product Model

Weather manages:

* current location
* saved cities
* current conditions
* hourly forecast
* multi-day forecast
* atmospheric detail data
* locally cached weather data
* user preferences
* units
* provider configuration

The application owns its presentation, local state, and caching.

OpenWeather is a data source, not part of the application architecture or visual language.

---

## Core v1 Capabilities

### Current weather

Display:

* location
* current temperature
* condition
* high / low
* feels-like temperature
* visibility
* wind speed and direction
* humidity
* pressure
* precipitation data
* UV / air-quality information where available from the free APIs
* data timestamp

Current conditions are the dominant element of the interface.

### Hourly forecast

Provide a compact forward-looking forecast using the data available from the OpenWeather free plan.

The first screen should make the immediate coming hours easy to scan.

### Multi-day forecast

Provide the forecast horizon available through the free OpenWeather APIs.

For v1 this means accepting the free-plan limitation rather than introducing billing solely to obtain a longer forecast.

The UI must not assume that every provider supplies exactly seven days.

### Locations

Support:

* current device location with permission
* manual city search
* saved cities
* switching between saved cities

Location permission must not be required to use the application.

### Offline and caching

The installed application should remain useful during temporary loss of connectivity.

Cache:

* latest successful weather data
* saved locations
* preferences
* the locally configured provider credential (not the weather payload)

Stale weather must be clearly identifiable as stale.

Never present old cached data as newly retrieved weather.

Do not depend on a service worker, HTTPS origin, or browser cache for this behavior.

### Installation

The application must install as a native Android APK and behave as a normal GrapheneOS / Pixel application.

---

# Design Language

## Direction

The application should feel like a **weather instrument**, not a lifestyle dashboard.

Primary references are:

* late-1970s industrial retro-futurism
* utilitarian spacecraft / industrial instrumentation
* 1980s noir retro-future interfaces
* technical Japanese cyberpunk interface language
* restrained Soviet-era industrial design

The result must be original rather than copying a specific film or interface.

## Visual principles

### Graphite first

Dark mode is the primary design.

Use:

* graphite black
* charcoal surfaces
* muted off-white text
* restrained cyan / teal instrumentation accents
* restrained amber informational accents
* rare warning red where semantically justified

Avoid:

* rainbow gradients
* glossy glassmorphism
* excessive blur
* neon cyberpunk
* decorative chrome
* photographic city wallpaper
* Material-looking card soup

### Technical, not theatrical

The interface may use:

* thin technical dividers
* grids
* inset instrument areas
* compact uppercase labels
* restrained telemetry-style metadata
* geometric weather glyphs
* range plots
* gauges where they communicate information better than text

Decoration must never compromise readability.

### Hierarchy

The primary information order is:

**Now → Next hours → Next days → detailed atmospheric data**

The first screen must remain calm.

Weather details may extend below the initial viewport rather than competing equally with current conditions.

### Android behavior without Android cosplay

Use established Android / Material conventions where they improve:

* navigation
* touch targets
* accessibility
* system font scaling
* gestures
* motion
* platform integration

Do not attempt to visually imitate Google's weather applications.

Compose and Material components may be used as plumbing. They must not impose a lifestyle-dashboard look.

---

# Screens

Initial navigation model:

* **Today**
* **Radar**
* **Cities**
* **Settings**

## Today

Primary weather instrument.

Contains:

* location/header
* current conditions
* hourly forecast
* multi-day forecast
* atmospheric details

## Radar

Map-oriented weather view.

Initial implementation depends on useful map/radar data being available without compromising the free-first product constraint.

The screen may begin smaller than the eventual meteorological visualization system.

## Cities

Manage and switch saved locations.

## Settings

Manage:

* units
* location behavior
* refresh behavior
* weather provider configuration where appropriate
* local OpenWeather credential
* application information

---

# Technical Direction

Initial target:

* Android
* GrapheneOS / Pixel first
* Kotlin
* Jetpack Compose
* native Android application

Initial application type:

* installable APK
* portrait first on phone

Desktop browsers, PWAs, WebViews, Capacitor, Trusted Web Activities, and hybrid shells are not the product.

## Local storage

Use an Android-native persistence mechanism suitable for structured local state, cached weather, saved cities, and a locally stored API credential.

Do not introduce a server-side database for the initial single-user application.

Do not use IndexedDB, localStorage, or a service worker.

The exact store (for example DataStore, Room, or encrypted preferences for the credential) is chosen in the authorized native phase that first needs it.

## Weather provider

Initial provider:

**OpenWeather free APIs**

The provider integration must sit behind a small internal boundary so application code does not become shaped around OpenWeather response objects.

Normalize provider responses into application-owned weather models.

This is a replaceability boundary, not an invitation to build a generic provider framework.

## API credentials

The OpenWeather key is configured and stored **locally at runtime** on the device.

Do not bake a reusable private key into the APK and pretend it is secret.

Do not build account infrastructure merely to solve credential storage.

Do not require a backend.

---

# Product Principles

## Information before decoration

Every visual element must either communicate weather state, hierarchy, interaction, or system status.

## Weather first

AI is not a v1 product requirement.

Do not add chatbot surfaces, generated commentary, or AI features merely because they are fashionable.

They may be evaluated later if they solve an actual weather-use problem.

## Free first

v1 should operate using OpenWeather functionality available without paid API usage.

Do not introduce billing dependencies merely to obtain:

* 16-day forecasts
* historical weather
* premium forecast horizons

Paid capabilities may be reconsidered later.

## Provider replaceability

OpenWeather must be replaceable without redesigning the application.

Do not, however, build a generic plugin system.

## Offline-aware

A weather application that immediately becomes blank because the network disappeared is badly behaved.

Preserve the last useful state and communicate its age.

## Privacy-conscious

Request only permissions required for a user-visible capability.

Location must be optional and user-initiated.

Avoid analytics, advertising SDKs, trackers, telemetry, and unnecessary external dependencies.

## KISS

Do not introduce infrastructure for hypothetical scale.

Prefer a small, understandable application over a framework exhibition.

Do not add widgets, WorkManager periodic sync, notifications, or radar merely because Android can host them. Those require explicit later phase contracts.

---

# Initial Non-Goals

The initial product does not include:

* user accounts
* cloud synchronization
* social features
* advertising
* analytics / tracking
* AI assistant
* historical weather
* paid OpenWeather APIs
* 16-day forecast
* arbitrary weather-provider plugins
* generic dashboard builder
* desktop-native application
* iOS-native application
* Progressive Web App
* WebView / Capacitor / Trusted Web Activity / hybrid shell
* widgets
* watch-face integration
* severe-weather push infrastructure
* background location tracking
* background periodic sync

These may be reconsidered by later phase contracts.

---

# Development Factory Rules

## Phase Boundaries

A phase is an immutable execution boundary.

Tasks may be refined inside the active phase, but its:

* goal
* scope
* exclusions
* acceptance criteria
* handoff contract

must not be expanded during implementation.

Work discovered outside the active phase is recorded as deferred work.

It is not implemented.

A completed task does not mean a completed phase.

The implementation agent must stop when the active phase handoff contract has been satisfied.

It must never begin the next phase automatically.

## Task Availability

`PHASES.md` contains the product roadmap.

Detailed task files are created only for the currently authorized phase.

Future phase task files should not be created in advance.

Historical PWA task files under `tasks/phase-0.md` … `tasks/phase-3.md` are prototype evidence. They are not native Android phases.

## Roles

### Roboticist / Project Owner

Defines intent, evaluates the actual product, and authorizes phase transitions.

### Planner

Maintains project structure, phase contracts, architecture boundaries, and acceptance criteria.

### Dr. Watson / External Observer

Inspects implementation, questions assumptions, identifies risks, and proposes deferred work.

External review does not automatically alter active scope.

### Coder Team

Implementation may use separate:

* coder planner
* coder executor
* coder tester
* coder team lead

roles.

All coder roles remain constrained by the active phase contract.

---

# Information Management

## Decision Records

Use `decisions/` when future work must respect an architectural or product decision.

Decision records contain:

* Status
* Context
* Decision
* Consequences

Do not create decision records merely to narrate implementation.

Do not silently rewrite accepted historical records. Supersede them with a new record.

## Documentation

Use `docs/` for durable technical knowledge such as:

* OpenWeather API behavior
* normalized weather schema
* caching and staleness rules
* Android location permission behavior
* credential storage
* build and install procedures
* design-system rules

## Task Notes

Use the current phase task file for:

* implementation progress
* tests performed
* test results
* files changed
* temporary findings
* known limitations

## Deferred Work

Useful discoveries outside the active phase are recorded rather than implemented opportunistically.

## The Rule

If forgetting something could cause a future agent to make the wrong implementation choice, record it.

Otherwise, don't.
