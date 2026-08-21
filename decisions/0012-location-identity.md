# 0012 — Location identity and deduplication

## Status

Accepted

## Context

Search can return the same place twice. Saved cities must not multiply. Weather cache must key by place, not by display name.

## Decision

A place’s identity is `lat.toFixed(4):lon.toFixed(4)`. That string is both `Place.id` and `Place.cacheKey`. Saving an existing key returns the stored row. Device location is not auto-saved, so GPS jitter does not create extra saved cities.

## Consequences

UI, catalog, and cache all use this key. Do not dedupe by display name. Changing rounding precision is a breaking identity change.
