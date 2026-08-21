# 0005 — Font strategy

## Status

Accepted

## Context

Typography needs a technical character without sacrificing legibility or offline install size.

## Decision

Use the platform UI sans stack:

`ui-sans-serif, system-ui, "Segoe UI", Roboto, "Liberation Sans", sans-serif`

Temperature and numeric telemetry use `font-variant-numeric: tabular-nums`. Labels use wide tracking and uppercase rather than a display font.

No webfont files are shipped in Phase 0.

## Consequences

The installed PWA does not depend on a font CDN. A self-hosted industrial face may be added later if system fonts prove too generic on GrapheneOS, but that is not required for Phase 0.
