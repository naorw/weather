# Design System

Graphite instrumentation. Not a lifestyle dashboard. Not Material card chrome. Not neon.

Token names below are the durable visual contract. Native Compose should reproduce these values; CSS custom properties were the PWA implementation and are not required.

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
| Body | `--text-body` | readable body size |
| Technical metadata | `--text-meta` | small, amber in the hero |
| Navigation label | `--text-nav` | uppercase, tracked |

Font stack: platform UI sans. See `decisions/0005-font-strategy.md` (system fonts; no CDN).

## Spacing

A tight scale from extra-small inset (~4 dp) through large section gaps. Prefer a named scale over ad-hoc values.

## Geometry

| Token | Intent |
| --- | --- |
| `--radius-container` | ~8 dp containers |
| `--radius-module` | ~4 dp controls |
| `--border-width` | 1 dp hairline dividers |
| `--touch-min` | 48 dp nav and buttons |

Corners stay tight. No pill navigation.

Dividers are hairline `--color-border` lines, not shadows or blur.

## Weather glyphs

Stroke glyphs, current color, square caps. Categories: clear, partly-cloudy, cloudy, overcast, drizzle, light-rain, rain, heavy-rain, thunderstorm, light-snow, snow, fog, unknown. Unknown is a circle with a mark.

## Hierarchy

**Now (hero) → Next hours → Next days → atmospheric detail**

The temperature is the dominant element. The near-term strip is horizontal. Days are compact rows with a shared-scale range bar. Wind uses a compact compass mark plus readout. Remaining atmosphere is a labeled list, not a grid of equal cards.

Hero high/low prefer today’s daily summary when that local date exists; otherwise the current-condition station envelope; otherwise an em dash (missing), never a fake zero.

## Partial days

Muted `part.` on incomplete first/last forecast days. See `decisions/0011-partial-day-marker.md`.

## Loading / empty / error

Same graphite instrument frame. Loading says “Acquiring weather” with no numeric placeholders. Empty is “No usable weather payload.” Provider errors use stable error codes with human titles (Credentials, Offline, Rate limited, …) and never include keys or raw payloads. A failed refresh keeps the last valid snapshot and shows a short amber note.

## Constraints

- Minimal blur and decoration
- No photographic backgrounds
- Accents are sparse: teal for location/active/range, amber for timestamp/meta
- Precipitation percentages also use teal so rain chance is not color-only (the `%` value is present)
- Focus/selection must remain visible
