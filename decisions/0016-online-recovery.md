# 0016 — Online recovery trigger

## Status

Accepted

## Context

Phase 3 must recover after connectivity returns without background periodic sync.

## Decision

Refresh the current location on (1) explicit Today Refresh, (2) the browser `online` event, (3) Today becoming visible. Deduplicate with the existing in-flight generation token. Do not add timers, `setInterval`, or Background Sync.

## Consequences

Later phases that want periodic update must not silently add polling here. `online` can fire spuriously; reuse in-flight work instead of stacking requests.
