# 0022 — Weather map overlay (OpenWeather Maps 1.0)

## Status

Accepted

## Context

Weather Maps 2.0 is paid. The free OpenWeather plan includes Weather Maps 1.0 raster tiles. These are **not** observed radar.

## Decision

v1 overlays are OpenWeather Maps 1.0 HTTPS tiles:

`https://tile.openweathermap.org/map/{layer}/{z}/{x}/{y}.png?appid={runtime key}`

Supported layers:

| UI name | `{layer}` | Meaning |
| --- | --- | --- |
| Precip map | `precipitation_new` | OpenWeather precipitation **map** (model field, ~3-hour update). Not observed radar. |
| Cloud cover | `clouds_new` | OpenWeather cloud-cover **map**. Not IR satellite. |

The same runtime key as weather APIs (`ApiKeyStore`). Never bake the key into the APK. Overlay off (`None`) is valid. Missing key: basemap still works; overlay disabled.

Do not expose Maps 2.0, RAPIDS, or paid animation.

## Consequences

UI must not label these layers “radar”. Legend is qualitative against OpenWeather’s default palette, not an invented mm scale. Tile hosts: `tile.openweathermap.org`.
