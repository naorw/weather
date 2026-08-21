# Development

Weather is a Vite TypeScript PWA. There is no application server.

## Prerequisites

- Node.js 22 or newer
- npm 10 or newer
- A Chromium-based mobile browser for install testing (Vanadium / Chrome)

## Install

```sh
npm install
```

## Local run

```sh
npm run dev
```

Opens a development server. Hash routes:

- `#/today`
- `#/radar`
- `#/cities`
- `#/settings`

Service-worker caching is not the thing to test in `vite` dev mode. Use a production preview for PWA checks.

## Production build

```sh
npm run build
npm run preview
```

`preview` serves `dist/` with the generated service worker.

## Tests

```sh
npm test
```

Watch:

```sh
npm run test:watch
```

Lint:

```sh
npm run lint
```

## PWA install testing

1. Build and preview (`npm run build && npm run preview`).
2. Open the preview URL on the phone (same network) or use port forwarding.
3. Confirm the browser offers Add to Home screen / Install.
4. Launch the installed icon and confirm it opens without browser chrome (`display: standalone`).
5. Manifest: `public/manifest.webmanifest`.
6. Icons: `public/icons/weather-192.png`, `weather-512.png`, `weather-512-maskable.png`.

Chrome DevTools → Application → Manifest should list name `Weather`, id `org.radilabs.weather`, display `standalone`.

## Service-worker debugging

1. Use the **preview/production** build, not `npm run dev`.
2. DevTools → Application → Service Workers.
3. Confirm a worker is activated for the origin.
4. DevTools → Application → Cache Storage should contain precached shell assets.
5. After a successful first load, set DevTools Network to Offline and reload. The shell (navigation + static Today) must still appear.
6. “Update on reload” helps while iterating. A stuck worker can be unregistered from the same panel, then the page hard-reloaded.

`vite-plugin-pwa` injects Workbox `generateSW` during `vite build`.

## Storage reset / debugging

Phase 0 proof IndexedDB (Settings probe only):

- Name: `org.radilabs.weather.phase0`
- Store: `kv`

Phase 3 application IndexedDB:

- Name: `org.radilabs.weather`
- Stores: `places`, `snapshots`, `kv` (`active`, `order`)

DevTools → Application → IndexedDB.

In the app: **Settings → Run storage probe** writes a timestamp to the proof store only; **Reset proof store** clears that proof store, not saved cities or weather cache.

See `docs/locations.md` and `docs/weather-cache.md`.

## Weather data (Phase 1–3)

Copy `.env.example` to `.env` and set `VITE_OPENWEATHER_API_KEY`. See `docs/credentials.md`.

After creating or changing `.env`:

- `npm run dev`: stop the process and start it again, or press `r` in the Vite terminal. A browser refresh alone does **not** reload env. You do **not** need `npm run build`.
- `npm run preview` / installed production build: run `npm run build` again so the key is inlined, then preview.

Dev (`npm run dev`) proxies `/ow` to OpenWeather so a phone on the LAN only needs to reach your computer. Production still calls `api.openweathermap.org` directly.

Firefox: `localhost` and `http://192.168.x.x:5173` are **different origins**. Cache Storage can show the LAN IP with empty caches; that is not IndexedDB. Unregister service workers for **both** hosts (about:serviceworkers). Then Ctrl+Shift+R.

Today loads the active location (first-run default Stockholm) through the provider, then caches the snapshot per location. Cities search uses OpenWeather geocoding on the same key.

- Optional live test: `npm test` runs `tests/weather/live.test.ts` when that env var is set.

If a request says `network`, the key was probably loaded (missing key is `auth`). A URL that works in the address bar is not the same as `fetch()` from the app.

Firefox: never pass a detached `fetch` reference; call `globalThis.fetch(...)`. A leftover service worker on `localhost` vs a LAN IP is a different origin.

Owner live check (2026-08-21): Stockholm current, 40-point forecast, 6 aggregated days, air quality fair.

## Browser limitations discovered

- `npm run dev` does not represent production service-worker behavior. Always verify offline shell on `preview`.
- Hash routing (`#/today`) avoids server rewrite configuration for GitHub Pages-style static hosts.
- Standalone install and `beforeinstallprompt` availability vary by Android browser. Vanadium / Chromium is the intended path.
- System fonts differ across GrapheneOS and stock Android; tabular numbers depend on the UI face.
- Firefox requires `window.fetch`; a copied `fetch` function throws TypeError.
- OpenWeather CORS/OPTIONS is unreliable from the browser; Vite `/ow` proxy is used on local hosts.
- Geolocation needs a secure context. `http://192.168.x.x` phone preview is typically insecure; use HTTPS, localhost, or the installed PWA origin.
