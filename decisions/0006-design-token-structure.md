# 0006 — Design token structure

## Status

Accepted

## Context

Later screens must reuse the same graphite instrument language.

## Decision

Tokens live as CSS custom properties on `:root` in `src/styles/tokens.css`.

Groups:

- `--color-*` surfaces, text, accents, warning, disabled, focus
- `--space-1` … `--space-7` spacing scale
- `--radius-container`, `--radius-module`, `--border-width`, `--touch-min`
- `--text-*` typography roles
- `--tracking-label`

Components consume tokens; they do not introduce one-off hex colors when a token exists.

## Consequences

New visual values should be added as tokens first. Documentation in `docs/design-system.md` is the human-readable companion to this file.
