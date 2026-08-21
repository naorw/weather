# Staleness

Thresholds apply to `fetchedAtMs` on the cached/live snapshot, compared with `Date.now()`.

## Thresholds

| Class | Rule |
| --- | --- |
| missing | No usable cache for the active location |
| fresh | Age under 30 minutes (`FRESH_MS`) |
| stale | Age ≥ 30 minutes |
| live | Just replaced by a successful provider fetch (not shown as cached) |

Thirty minutes matches free-plan weather that is not polled: current-enough for daily use, old enough to admit it is not a live ticker.

## Visual semantics

Today uses graphite/amber technical labels, not alarm red:

- **CACHED** + `UPDATED … AGO` — showing cache, still inside 30 minutes, live refresh not yet confirmed
- **STALE** + `UPDATED … AGO` — cache older than 30 minutes, or failed refresh keeping that cache
- No CACHED/STALE badge after a successful live fetch

Age wording: `JUST NOW`, `N MIN AGO`, `N H AGO`, `N D AGO`. Example: `STALE UPDATED 47 MIN AGO`.

Weather values stay fully visible. Ordinary offline use is not styled as a severe-weather alert.

## Recovery

Stale/cached badges clear only after a successful refresh for the **current** location. A failed refresh keeps the last snapshot and its age. Switching location never paints another city’s in-flight result.
