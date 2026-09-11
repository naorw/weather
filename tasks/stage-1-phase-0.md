# Stage 1 — Phase 0: Current Context and Map Semantics

Status: **authorized / not started**

Release target: **v0.1.1**

## Goal

On cold start, when location permission is already granted, prefer the device's current location instead of silently showing a stale previously active city. Also rename the existing map surface so it is not presented as observed radar.

## Scope

- On cold app start, if location permission is already granted, resolve device location, make it active for the session, reverse-geocode using the existing path, and refresh weather for it.
- If location lookup fails, fall back safely to the previous active/cached location without erasing cache or claiming that fallback is current location.
- If permission is not already granted, do not request it automatically; preserve current saved-location behavior.
- Automatic current-location selection is startup-only. A city the user explicitly activates during the running session wins until the app is restarted.
- Do not auto-save transient device locations into Saved Cities.
- Bottom navigation label becomes `Map`.
- Map screen header becomes `WX MAP`.
- Keep existing `PRECIP MAP` and `CLOUD COVER` terminology and do not call model-derived overlays observed radar.
- Prepare `versionName = "0.1.1"`, increment versionCode, and reuse the v0.1.0 production signing identity.

## Explicit Exclusions

- Background location.
- Startup permission prompt.
- Periodic/background refresh.
- Notifications.
- Widgets or watch work.
- New weather providers or paid data.
- Observed or historical radar.
- New map layers.
- Accounts or cloud sync.
- Broad refactor unrelated to this phase.

## Acceptance Criteria

1. With location permission already granted, cold launch resolves current location and Today shows weather for it.
2. Startup does not silently present a stale previous city as current context.
3. Location failure falls back cleanly to the previous active/cached location.
4. Without location permission, startup does not prompt and existing behavior remains usable.
5. Device startup does not create duplicate saved-city entries.
6. Manual city choice wins for the rest of the running session, including after tab navigation.
7. Offline/cache guarantees remain intact.
8. Navigation says `MAP`; map header says `WX MAP`.
9. Existing map overlays remain truthful and functional.
10. v0.1.1 reuses the v0.1.0 signing identity.
11. Existing tests pass and focused startup/session tests are added where practical.

## Required Verification

Test on Pixel / GrapheneOS:

- cold launch with permission already granted;
- location unavailable / failed lookup fallback;
- no permission and no startup prompt;
- manual city selection followed by tab navigation;
- offline fallback behavior;
- Map / WX MAP naming;
- production signing continuity from v0.1.0.

Because this phase changes startup/location lifecycle behavior, obtain an independent reviewer or Watcher pass before owner acceptance.

## Handoff Contract

Before this phase can be declared complete:

- startup location behavior must be demonstrated on a real Pixel / GrapheneOS device;
- no-permission and failed-location fallback paths must be demonstrated;
- manual-selection precedence for the running session must be demonstrated;
- offline/cache behavior must be rechecked;
- map naming must be visually reviewed;
- tests and build results must be recorded;
- signing continuity for v0.1.1 must be verified;
- known limitations and deferred work must be recorded.

Then STOP.

Do not begin Stage 1 Phase 1 automatically.
