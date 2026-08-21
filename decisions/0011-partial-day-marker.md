# 0011 — Partial-day forecast marker

## Status

Accepted

## Context

Free 3-hour forecasts leave the first and last local calendar days incomplete. Phase 1 marks those days `partial: true`. Phase 2 needs a stable way to show that without alarm styling.

## Decision

Incomplete days show a muted uppercase `part.` label in the multi-day row, with title text “Incomplete forecast coverage”. Complete days leave that slot empty. Range bars still use the available points for that day.

## Consequences

Later screens that list daily summaries should keep this marker (or an equivalent muted completeness cue) rather than treating a 3-point first day as a full diurnal range.
