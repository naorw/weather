# 0004 — PWA service worker

## Status

Accepted

## Context

Phase 0 requires an installable PWA with application-shell caching. Weather-data caching is out of scope.

## Decision

Use `vite-plugin-pwa` with Workbox `generateSW`:

- `registerType: autoUpdate`
- precache JS, CSS, HTML, icons, and the web manifest
- `navigateFallback: index.html`
- static manifest at `public/manifest.webmanifest`
- `display: standalone`, `start_url: ./`

The service worker caches the application shell only.

## Consequences

Offline weather payloads are not stored by the service worker. Phase 3 must add an explicit weather-cache strategy rather than overloading this precache.
