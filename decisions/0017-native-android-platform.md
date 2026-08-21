# 0017 — Native Android platform

## Status

Accepted

Supersedes, for v1 shipping platform and implementation path:

- `0002` Frontend toolchain (Vite / vanilla TypeScript / Vitest)
- `0003` IndexedDB access
- `0004` PWA service worker
- `0008` OpenWeather credential strategy (`VITE_*` / browser-visible key)

Also supersedes PWA-specific *implementation paths* in:

- `0001` (web manifest, `public/icons`, theme-color packaging)
- `0005` (CSS webfont/PWA size rationale; system-font *intent* remains)
- `0006` (CSS custom properties in `src/styles/tokens.css`)
- `0009` (`src/weather` TypeScript module layout)
- `0013` (IndexedDB database and object stores)
- `0016` (browser `online` event / service-worker shell)

Those older records remain historical. Their product intent (identity `org.radilabs.weather`, graphite tokens, provider boundary, cache-per-location, no polling) still applies unless a later native-phase decision replaces it.

Does **not** rewrite or delete `0001`–`0016`.

## Context

The PWA prototype demonstrated the product concept and visual language, but the deployment/runtime model does not match the intended product.

The actual product needs:

- installable APK
- true native Android application lifecycle
- Android location permissions
- local runtime API-key configuration/storage
- reliable local/offline behavior without HTTPS-origin/service-worker requirements
- direct GrapheneOS / Pixel usage
- future access to native Android capabilities where explicitly phased

## Decision

Weather v1 will be implemented as a **native Android application using Kotlin and Jetpack Compose**.

The PWA implementation is abandoned as the shipping v1 platform.

No WebView wrapper, Capacitor wrapper, Trusted Web Activity, or hybrid shell may be introduced as a shortcut.

## Consequences

- existing web/PWA implementation is not carried forward by default
- product/design/provider behavior may be reimplemented cleanly in Kotlin
- Git history preserves the prototype
- old PWA-specific decisions remain historical but are superseded where listed above
- native Android decisions now govern future implementation
- native Phase 0 is **not** authorized by this record; the owner/planner must open it explicitly
