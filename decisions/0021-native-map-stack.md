# 0021 — Native map stack (MapLibre + OpenFreeMap)

## Status

Accepted

## Context

Phase 4 needs a native map on GrapheneOS without Google Play Services or a WebView. osmdroid is archived.

## Decision

Use **MapLibre Native Android** (`org.maplibre.gl:android-sdk`) with the public **OpenFreeMap Dark** style:

`https://tiles.openfreemap.org/styles/dark`

No MapLibre/MapTiler/Google key. No Play Services location plugin.

Basemap data is OpenStreetMap. OpenFreeMap public instance is free with required attribution (MapLibre attribution control remains enabled).

## Consequences

Radar contacts `tiles.openfreemap.org` (style, vector tiles, glyphs/sprites as referenced by that style). Do not switch to Google Maps or a WebView wrapper. Changing the basemap host is a product decision.
