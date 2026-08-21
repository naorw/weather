# 0001 — Application identity

## Status

Accepted

## Context

The PWA needs a stable name, short name, and application ID for the web manifest, standalone install, and later packaging. PikaTalk uses a Radilabs reverse-DNS identity.

## Decision

- Application name: `Weather`
- Short name: `Weather`
- Application ID: `org.radilabs.weather`
- Description: `A weather instrument for Android. Current conditions, forecast, and atmospheric detail.`
- Initial version: `0.0.0`
- Theme color / background color: `#14171b`
- Icons: `public/icons/weather-{size}.png` with a maskable 512 variant; SVG source at `icons/weather.svg`

## Consequences

Future phases must keep `org.radilabs.weather` as the installed application identity unless the owner explicitly changes it. Icon filenames may gain sizes, but the `weather-` prefix stays.
