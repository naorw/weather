# Native Phase 5 — Daily-Use Polish

## Status

**Accepted** 2026-08-21 on Pixel / GrapheneOS. First public release **Weather v0.1.0** is authorized for publication.

Remaining non-blocking polish and extra testing is deferred to **v0.1.1** or a future explicitly authorized phase.

Do not begin Phase 6. Do not create `tasks/phase-6.md`.

---

Repository:

`https://github.com/naorw/weather`

Authoritative phase contract:

`https://github.com/naorw/weather/blob/main/PHASES.md`

Current accepted baseline:

`b944e7e`

Native Phase 4 is accepted on Pixel / GrapheneOS.

The owner explicitly authorizes:

**Native Phase 5 — Daily-Use Polish**

This phase ends in the first real release:

**v0.1.0**

Do not add new product scope.

Do not invent Phase 6 work.

## Read First

Read in order:

1. `PROJECT.md`
2. `PHASES.md`
3. `TASKS.md`
4. `tasks/README.md`
5. `tasks/phase-4.md`
6. `docs/handoffs/phase-4.md`
7. current `README.md`
8. `docs/development.md`
9. all native decisions `0017` onward
10. current design-system / weather / cache / map docs
11. private local audit report under `reviews/` if present

The committed Phase 5 contract in `PHASES.md` is immutable.

The private audit report is implementation input only. It must **not** be copied verbatim into public Git documentation.

If a genuine contradiction blocks the phase, STOP and report it.

## Authorization Files

Before implementation:

* update `TASKS.md` so Native Phase 5 is the only authorized phase
* update `tasks/README.md`
* create `tasks/phase-5.md` from the committed Phase 5 contract plus the accepted release-readiness findings below

Do not create future-phase task files.

## Goal

Make Weather a dependable personal daily-driver and produce the first real release:

**v0.1.0**

Phase 5 is about:

**reliability + packaging + lifecycle correctness + accessibility + performance + release hygiene**

Not new features.

## Release Version

Normalize release versioning to:

* `versionName = "0.1.0"`
* `versionCode = 6`

Do not continue using phase numbers as public versions.

Replace any hardcoded UI version string with runtime package metadata.

Settings must show the actual installed app version dynamically.

## Mandatory Audit Fixes

The following are release-critical and must be fixed before v0.1.0.

### B-1 — Versioning / Dynamic Version

Fix:

* current `0.4.0` / versionCode 5
* hardcoded Settings version string

Use runtime `PackageInfo` / package metadata.

### B-2 — MapLibre Lifecycle Ordering

Fix Radar `MapView` lifecycle ordering so:

`onCreate()` happens before lifecycle observer registration can synchronously dispatch `onStart()` / `onResume()`.

Respect the MapLibre lifecycle contract.

Add a focused regression test where practical, or at minimum document and manually validate navigation into Radar from an already-resumed Activity.

### B-3 — Cache Write Must Never Crash App

A cache write is optional infrastructure.

Failure to write cache must not crash Weather.

Fix:

* unhandled `FileSnapshotCache.write()` exceptions
* propagation through `WeatherSession`
* unhandled refresh coroutine failure path

A successful network weather result must remain usable in memory even if persistence fails.

Disk-full / I/O failure must degrade gracefully.

## High-Priority Release Fixes

These should be resolved in Phase 5 unless a fix materially destabilizes the app.

### H-1 — Disk I/O Off Main Thread

Move snapshot-cache disk reads/writes off the main thread.

Avoid synchronous file I/O on UI-critical paths.

Keep implementation simple.

Do not create a repository framework for this.

### H-2 — Coroutine Failure Safety

Add robust failure containment around refresh/search/location coroutines.

Unexpected exceptions should not crash the process.

Do not swallow cancellation incorrectly.

Cancellation must remain cancellation.

Use structured error handling appropriate to the current architecture.

### H-3 — Remove Unsafe `as WeatherError`

Replace fragile unchecked cast with safe error handling.

Do not allow future exception-type changes to become `ClassCastException`.

### H-4 — Proper Launcher Icon

Add a release-quality Android launcher icon setup.

Provide:

* adaptive icon where appropriate
* correct Android density/resource behavior
* round/icon support as required

Preserve the existing Weather mark / visual identity.

Do not redesign branding.

### H-5 — Real Release Signing

Create a proper release-signing path.

Requirements:

* release APK must not use Android debug key
* signing key stays outside Git
* passwords stay outside Git
* no private signing material is ever committed
* Gradle reads signing configuration from local/environmental configuration
* release documentation explains setup
* build fails clearly or provides a documented unsigned path if release signing config is missing

Do not generate or commit a keystore automatically unless explicitly instructed by owner.

Document exact owner-side steps for generating/placing the release keystore.

### H-6 — R8 / Minification

Evaluate release shrinking/minification.

Preferred:

* enable R8
* shrink resources where safe
* add required Gson keep rules
* ensure MapLibre still works

But:

**Do not destabilize v0.1.0 merely to reduce APK size.**

If enabling R8 introduces meaningful risk or unexplained runtime failures, document the result and defer it.

Do not treat APK size alone as a blocker.

## Accepted Medium-Value Fixes

Take these if they are narrow and safe.

### Atomic Cache Writes

Replace direct final-file writes with safe temp-write + replace/rename semantics where practical.

A process death during cache write should not destroy the previous valid cache.

### Cancellable Device Location

Improve `DeviceLocator` so one-shot location can be cancelled cleanly when the calling coroutine is cancelled.

Avoid blocking an I/O thread for up to 12 seconds if a suspend/callback solution is straightforward.

Do not change permission behavior.

### Exhaustive Today State Rendering

Replace non-exhaustive conditional state rendering with a sealed-state `when(state)` style where appropriate.

Future states should fail at compile-time rather than silently render nothing.

### Typed LIVE/CACHED/STALE Semantics

Do not infer live state by comparing strings such as `"LIVE"`.

Use typed/explicit state.

Keep user-facing labels unchanged unless polish clearly improves them.

### Dead-Code Cleanup

Remove clearly unused code identified by audit, such as obsolete placeholder/unused helpers, only where verified unused.

Do not perform broad speculative cleanup.

### User-Facing Settings Copy

Remove internal development-language strings like:

`Units and provider UI belong to later phases`

Replace with user-facing wording or omit them.

### Splash / Launch Polish

Use Android’s normal splash behavior where appropriate.

Keep it restrained and consistent with graphite Weather identity.

No splash animation project.

## Watcher / Audit Observations

Review and improve where low-risk:

* redundant refresh/network work
* returning to Today causing unnecessary fetches
* connectivity callback causing duplicate work
* permission-denied edge behavior
* font scaling extremes
* map memory handling / `onLowMemory`
* corrupted cache handling

Do not turn Phase 5 into an architecture rewrite.

The standard is:

**fix real daily-driver pain and release risks, defer theoretical improvements.**

## Network Efficiency

Phase 5 acceptance requires:

**no obvious high-frequency redundant API requests.**

Review current refresh triggers:

* app startup
* Today navigation
* connectivity restoration
* manual refresh

Avoid repeated overlapping fetches for the same active place when a fresh/in-flight result already exists.

Preserve:

* explicit refresh
* reconnect recovery
* cache-first startup

Do not add polling.

Do not add WorkManager.

## Accessibility

Perform and document a practical accessibility pass.

Check:

* common Pixel font scaling
* extreme-ish font scale where reasonable
* contrast
* TalkBack labels for important controls/data
* touch targets
* wind compass semantics
* weather glyph descriptions
* map controls
* Cities search/status
* API-key controls

Do not attempt full WCAG certification.

Fix obvious problems.

## Reliability

Validate and improve:

* corrupt cache entry handling
* cache write failure
* missing storage / I/O errors
* denied location permission
* permanently denied permission
* provider outage
* bad API key
* rate limit
* map/overlay failure
* airplane mode
* reconnect
* process restart
* app upgrade

No failure in Radar should take down Today/Cities/Settings.

## Packaging

Prepare a proper release artifact.

Target output:

`weather-v0.1.0.apk`

The release artifact must:

* use production signing
* report version `0.1.0`
* install and launch normally
* preserve release signing identity for future upgrades
* contain no API key
* contain no signing secret
* contain no private audit files

Also produce:

`SHA256SUMS`

containing the checksum of the release APK.

## Important Signing Transition

All accepted pre-v0.1 builds were debug-signed.

A production-signed `v0.1.0` APK may not install as an in-place upgrade over the currently installed debug-signed app.

This must be tested and documented honestly.

Do not attempt unsafe signature hacks.

If uninstall/reinstall is required for the first production-signed release:

* document that clearly
* warn that app-private settings/cache/API key will be removed by uninstall
* record the one-time transition
* future v0.1.x/v0.2.x releases must use the same production signing key so normal upgrades work

## GitHub Release

The intended distribution channel is GitHub Releases.

No Play Store work.

No AAB requirement.

No F-Droid work.

Prepare the repository for:

Tag:

`v0.1.0`

GitHub Release title:

`Weather v0.1.0`

Release assets:

* `weather-v0.1.0.apk`
* `SHA256SUMS`

Release notes should be concise and public-safe.

Include:

* first native Android release
* Today live weather instrument
* saved locations + device location
* offline cached weather
* precipitation/cloud map
* local runtime OpenWeather API key
* GrapheneOS / Pixel focus
* known limitations

Do not publish raw audit findings or private review documents.

## Git Operations

Phase 5 implementation may prepare release files and documentation.

Do not create/push the final `v0.1.0` tag or GitHub Release until:

1. owner Pixel regression passes
2. owner confirms production-signed APK installation path
3. Phase 5 is explicitly accepted

Implementation agent must stop at:

**implemented / awaiting owner acceptance**

before final release publication unless owner explicitly authorizes the release operation.

## Private Review Handling

`reviews/` is private/local and gitignored.

Rules:

* do not commit raw audit reports
* do not quote private audit line-by-line into public docs
* distill accepted fixes into `tasks/phase-5.md`
* public docs may describe remediated behavior and remaining known limitations
* no signing secrets/local paths in Git

## Tests

Preserve all existing tests.

Add focused tests for release-critical fixes where worthwhile:

* MapView lifecycle ordering logic if testable outside native integration
* cache-write failure behavior
* cache preservation on failed write
* typed live/cached/stale rendering logic
* coroutine error mapping
* redundant refresh suppression
* dynamic version presentation helper if separated
* corrupted cache behavior

Do not create hundreds of trivial tests.

## Full Regression

Before Phase 5 handoff, run a complete regression over:

### Today

* live load
* refresh
* cached startup
* stale state
* provider error
* missing key
* invalid key

### Cities

* search
* save
* remove
* switch
* restart persistence
* device location
* denied permission

### Offline

* airplane-mode launch
* cached weather
* failed refresh preserving data
* reconnect to LIVE

### Radar

* opens from Today/Cities/Settings
* MapView lifecycle stable
* saved-city focus
* device-location focus
* pan/zoom
* recenter
* precipitation
* cloud cover
* offline graceful behavior

### Settings

* API key save
* replace
* remove
* dynamic version display

### Packaging

* debug APK
* production release APK
* version metadata
* signing identity
* checksum

## Pixel / GrapheneOS Owner Acceptance

Prepare for final real-device acceptance.

Owner should validate at minimum:

1. release candidate installs according to documented signing-transition path
2. launches independently
3. version shows `0.1.0`
4. existing major screens work together
5. Today loads and refreshes
6. saved cities/device location work
7. offline launch still shows useful cached weather
8. reconnect returns to LIVE
9. Radar opens repeatedly from different tabs without lifecycle failure
10. precip/cloud overlays work
11. API-key save/remove works
12. denied permission remains graceful
13. font scaling remains usable
14. no obvious repeated network thrashing
15. visual coherence remains intact
16. no crash during normal daily use regression

## Documentation

Complete/update:

* `README.md`
* `docs/development.md`
* release build/signing docs
* troubleshooting
* known limitations
* release/install instructions
* first debug-signed → production-signed transition
* future upgrade rule: same signing key must be reused

Do not expose secrets.

## Handoff

Create:

`docs/handoffs/phase-5.md`

Record:

* status
* release candidate version
* audit findings addressed
* findings deferred
* lifecycle fixes
* persistence/cache fixes
* accessibility review
* network/performance observations
* release signing state
* APK path
* SHA256
* test results
* Pixel checks remaining
* known limitations
* GitHub Release readiness

End Phase 5 as:

**implemented / awaiting owner acceptance**

Do not declare v0.1.0 released yet.

## Hard Boundaries

Do NOT add:

* new major screens
* new weather providers
* paid weather features
* historical weather
* 16-day forecast
* widgets
* watch support
* notifications
* background periodic sync
* AI
* accounts
* cloud sync
* generic plugin architecture
* PWA/hybrid work
* Play Store integration

## Final Validation Before STOP

Before stopping:

* run full unit test suite
* build debug APK
* build production release APK
* verify production signature
* verify version `0.1.0` / versionCode 6
* verify no API key in APK/source
* verify no private review files in Git
* verify no signing secret in Git
* calculate release APK SHA256
* verify Phase 0–4 core behavior did not regress
* update Phase 5 handoff
* STOP

Do not self-authorize future work.
Do not tag or publish `v0.1.0` before owner acceptance.
