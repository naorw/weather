# 0015 — First-run location policy

## Status

Accepted

## Context

Today needs coordinates before the user has searched. Geolocation must stay optional.

## Decision

If no active place is stored, use Stockholm SE (`59.3293, 18.0686`) as a documented default. Do not request location permission to pick the first city. The user may replace it via search or explicit device location.

## Consequences

Normal operation after first use is whatever the user last activated. Do not reintroduce a hard-coded Stockholm fetch that ignores the stored active place.
