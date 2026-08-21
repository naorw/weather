# Design system — Phase 0

Graphite instrumentation. Not a lifestyle dashboard. Not Material card chrome. Not neon.

## Surfaces

| Token | Role | Value |
| --- | --- | --- |
| `--color-base` | page graphite | `#14171b` |
| `--color-surface` | nav / inset panels | `#1c2128` |
| `--color-surface-raised` | controls, range track | `#242b33` |
| `--color-text` | primary text | `#e6e4dc` |
| `--color-text-muted` | labels, secondary | `#9aa3ad` |
| `--color-accent` | cyan/teal instrument | `#3d9a9a` |
| `--color-accent-amber` | informational meta | `#c4923a` |
| `--color-warning` | warning/error | `#c45c4a` |
| `--color-border` | dividers | `#2e363f` |
| `--color-disabled` | disabled | `#5c6570` |
| `--color-focus` | focus ring | `#5eb8b8` |

Do not use pure `#000` as the only surface.

## Typography roles

| Role | Token | Treatment |
| --- | --- | --- |
| Current temperature | `--text-temp` | large, light weight, tabular |
| Location / title | `--text-location` | uppercase, tracked, teal |
| Section heading | `--text-heading` | uppercase, tracked |
| Primary data value | `--text-value` | tabular |
| Body | `--text-body` | 0.95rem |
| Technical metadata | `--text-meta` | small, amber in the hero |
| Navigation label | `--text-nav` | uppercase, tracked |

Font stack: system UI sans. See `decisions/0005-font-strategy.md`.

## Spacing

`--space-1` (0.25rem) through `--space-7` (3rem). Prefer these over ad-hoc pixels.

## Geometry

| Token | Value | Use |
| --- | --- | --- |
| `--radius-container` | 8px | settings probe panel |
| `--radius-module` | 4px | buttons |
| `--border-width` | 1px | hairline dividers |
| `--touch-min` | 48px | nav and buttons |

Corners stay tight. No pill navigation.

Dividers are 1px `--color-border` lines, not shadows or blur.

## Weather glyphs

Stroke SVG, `currentColor`, 24×24 viewBox, square caps. Phase 0 set: clear, partly-cloudy, overcast, drizzle, rain. Implemented in `src/glyphs.ts`.

## Hierarchy

**Atmosphere label → Now (hero) → Next hours → Next days → detailed atmosphere**

The temperature is the dominant element. Hourly data is a horizontal strip. Days are compact rows with a shared-scale range bar. Telemetry is a labeled list, not a grid of equal cards.

## Constraints

- Minimal blur and decoration
- No photographic backgrounds
- Accents are sparse: teal for location/active/range, amber for timestamp/meta
- Precipitation percentages also use teal so rain chance is not color-only (the `%` value is present)
- Focus: 2px `--color-focus` outline
