# 0008 — OpenWeather credential strategy

## Status

Accepted

## Context

The app is a static PWA. Bundled JavaScript cannot keep an API key confidential.

## Decision

Supply the key as `VITE_OPENWEATHER_API_KEY`. Do not add a backend in Phase 1. Do not commit `.env`. Document that the browser user can read the key.

Details: `docs/credentials.md`.

## Consequences

Later phases must not assume the key is secret. A proxy is a new product/ops decision, not a silent addition.
