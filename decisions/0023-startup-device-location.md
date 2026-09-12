# 0023 — Cold-start device location when already permitted

## Status

Accepted

## Context

v0.1.0 restored the last active place on launch. After the user had granted location in Cities, cold start still showed that previous city, which felt like stale current context.

## Decision

On **cold start only**, if location permission is **already granted**, resolve a one-shot device fix, reverse-geocode through the existing provider path, and `setActive` that `PlaceSource.Device` place for the session. Do **not** request permission at startup. Do **not** auto-save into Saved Cities.

Lookup/geocode failure keeps the previous active place and its cache. After the user explicitly activates another city, that choice wins until process restart; tab navigation must not re-run the startup device lookup.

## Consequences

This supersedes Stage 0 Phase 3's "no location on launch" rule only for the already-granted case. Historical Stage 0 docs remain unchanged. No background location.
