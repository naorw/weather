# 0014 — Stale weather thresholds

## Status

Accepted

## Context

Cached weather must not look live. Free current/forecast data is not a live ticker.

## Decision

Age is `now - fetchedAtMs`. Younger than 30 minutes is fresh-enough cache (`CACHED`). 30 minutes or older is `STALE`. A successful live fetch for the current location is `live` and clears those badges. Failed refresh never clears them.

## Consequences

Later phases must not treat cache as current without this classification. Changing `FRESH_MS` is a product decision, not a silent tweak.
