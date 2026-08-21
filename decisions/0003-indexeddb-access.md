# 0003 — IndexedDB access

## Status

Accepted

## Context

Phase 0 must prove structured persistence without implementing weather cache or saved cities.

## Decision

Use the native IndexedDB API through a small wrapper in `src/storage.ts`.

- Database name: `org.radilabs.weather.phase0`
- Object store: `kv`
- Operations: open, put, get, delete, clear

Do not add an IndexedDB library for this phase.

## Consequences

Later weather-cache and saved-city stores should use a **different database name or a new versioned schema**, not the Phase 0 proof key-value store. The wrapper pattern (Promise around IDBRequest) can be reused.
