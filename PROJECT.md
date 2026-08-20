# Weather

## General Information

**Weather** is a small, installable weather PWA designed primarily for **Android / GrapheneOS**.

The goal is not to build another generic weather dashboard.

The goal is to build a fast, visually distinctive weather instrument that presents useful weather information clearly, works well as an installed Android PWA, and remains useful even when connectivity is temporarily unavailable.

Initial weather data comes from the **OpenWeather free APIs**.

The weather provider must remain replaceable.

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

The installed PWA should remain useful during temporary loss of connectivity.

Cache:

* application shell
* latest successful weather data
* saved locations
* preferences

Stale weather must be clearly identifiable as stale.

Never present old cached data as newly retrieved weather.

### Installation

The application must be installable as a PWA on Android and behave sensibly when launched as a standalone application.

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

**Atmosphere → Now → Next hours → Next days → detailed atmospheric data**

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
* application information

---

# Technical Direction

Initial target:

* Android
* GrapheneOS
* modern mobile browsers supporting installable PWAs

Initial application type:

* responsive web application
* Progressive Web App
* mobile / portrait first

The application should also remain usable in a normal desktop browser, but desktop-specific UX is not a v1 priority.

## Local storage

Use browser-native storage suitable for structured local state and cached weather information.

Prefer IndexedDB for structured persistent data.

Do not introduce a server-side database for the initial single-user application.

## Weather provider

Initial provider:

**OpenWeather free APIs**

The provider integration must sit behind a small internal boundary so application code does not become shaped around OpenWeather response objects.

Normalize provider responses into application-owned weather models.

This is a replaceability boundary, not an invitation to build a generic provider framework.

## API credentials

The application must not casually embed a reusable private API credential into publicly served client code and pretend it is secret.

The implementation phase must explicitly determine an appropriate credential strategy for the intended deployment model.

Do not build account infrastructure merely to solve this unless actually required.

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

Location must be optional.

Avoid analytics, advertising SDKs, trackers, telemetry, and unnecessary external dependencies.

## KISS

Do not introduce infrastructure for hypothetical scale.

Prefer a small, understandable application over a framework exhibition.

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
* native Android application
* widgets
* watch-face integration
* severe-weather push infrastructure
* background location tracking

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

## Documentation

Use `docs/` for durable technical knowledge such as:

* OpenWeather API behavior
* normalized weather schema
* PWA behavior
* caching strategy
* browser limitations
* deployment procedures
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

