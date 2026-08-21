# 0010 — Daily forecast aggregation

## Status

Accepted

## Context

The free forecast is 3-hour steps, not daily rows. Phase 2 needs deterministic daily summaries.

## Decision

Use the rules in `docs/forecast-aggregation.md`: offset-based local dates, max/min temperature, severity-based representative condition, max probability, summed amounts, `partial` when fewer than eight points.

## Consequences

Phase 2 must show partial days as incomplete rather than as full 24-hour climate. Changing the severity table or completeness threshold is a contract change.
