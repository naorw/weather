# Radar and maps

Phase 4 map instrument. Not a GIS product. Not observed weather radar.

## Map library

**MapLibre Native Android** (`org.maplibre.gl:android-sdk`). BSD-2-Clause. No Google Play Services. No WebView.

`MapLibre.getInstance(applicationContext)` is called when Radar opens. Failure stays on Radar.

## Basemap

OpenFreeMap Dark style: `https://tiles.openfreemap.org/styles/dark`

OSM data. No API key. Attribution remains enabled. License: OpenFreeMap MIT; map data ODbL (OSM); style CC-BY as documented by OpenFreeMap.

Hosts: `tiles.openfreemap.org` (style JSON, vector tiles, glyphs/sprites referenced by the style).

## Weather overlay

OpenWeather **Maps 1.0** (free plan). Not Maps 2.0.

`https://tile.openweathermap.org/map/{layer}/{z}/{x}/{y}.png?appid={runtime key}`

| Control | Layer id | Meaning |
| --- | --- | --- |
| NONE | — | Overlay off |
| PRECIP MAP | `precipitation_new` | Precipitation **map**, model field, ~3 hours. Not radar. |
| CLOUD COVER | `clouds_new` | Cloud-cover **map**. Not IR satellite. |

Runtime key from `ApiKeyStore`. Overlay URLs are not logged. Missing key: basemap works; overlay note shown.

Legend for precipitation is qualitative (none → light → moderate → heavy) matching OpenWeather’s default palette intensity. OpenWeather does not publish a precise mm/h legend for Maps 1.0; do not invent one.

## Active location

Radar uses Phase 3 `WeatherSession.active()`. No second location model. Radar does not request location permission.

Initial camera: active coordinates, zoom 7. Recenter returns there. Cities switch then opening Radar focuses the new place.

## Map state

Pan/zoom are process-local. No bookmarks, routes, drawings, or offline region downloads.

## Failure

| Condition | Radar | Today / Cities |
| --- | --- | --- |
| Overlay 401/empty/network | Note; basemap usable | Unchanged |
| No API key | Overlay off; note | Unchanged |
| Style/tiles fail | Contained Radar error if MapView cannot start | Unchanged |
| Airplane mode | Cached Today still works; Radar shows whatever MapLibre still has in memory, otherwise empty/failed tiles. **Not** an offline map product. | Cache rules unchanged |

## Privacy

No analytics. Device coordinates are used only to center the camera and draw the active marker. Basemap tile requests imply the viewport (tile x/y/z) to OpenFreeMap. Overlay tiles imply the same viewport to OpenWeather. The API key is sent to `tile.openweathermap.org` as `appid` on overlay tiles only.

## Performance

MapView is created once per Radar composition. Overlay tiles are not rebuilt because Today recomposed. No polling. Overlay source is replaced only when the user changes layer.

## Permissions

No new permissions. Location remains Cities-owned.
