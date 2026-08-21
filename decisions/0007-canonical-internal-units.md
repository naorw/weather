# 0007 — Canonical internal units

## Status

Accepted

## Context

Normalized models must not change when a future Settings screen switches display units. OpenWeather is queried with `units=metric` and copied into these units.

## Decision

| Quantity | Canonical unit |
| --- | --- |
| Temperature | Degree Celsius |
| Wind | Metres per second |
| Pressure | Hectopascal |
| Precipitation | Millimetre |
| Visibility | Metre |
| Ratios | 0–100 percent |
| Time | Unix milliseconds UTC |
| Time zone | Offset seconds east of UTC |

Display conversion happens above the provider layer, not inside it.

## Consequences

Phase 2+ UI must convert from these units if the user prefers °F, km/h, or inches. Do not store provider-native mixed units in application models.
