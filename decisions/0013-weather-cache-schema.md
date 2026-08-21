# 0013 — Weather cache schema and versioning

## Status

Accepted

## Context

Offline Today needs the last successful normalized snapshot per location, not raw OpenWeather JSON.

## Decision

Store one record per `cacheKey` in IndexedDB database `org.radilabs.weather`, object store `snapshots`, `schemaVersion: 1`, `provider: "openweather"`. Invalid or other-version rows are deleted individually. Failed fetches do not write.

The Phase 0 proof database `org.radilabs.weather.phase0` remains separate.

## Consequences

Breaking snapshot-model changes increment `CACHE_SCHEMA_VERSION` and treat old rows as a cache miss. Do not mix raw provider payloads into this store.
