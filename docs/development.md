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

Phase 0 uses a proof IndexedDB database only:

- Name: `org.radilabs.weather.phase0`
- Store: `kv`

DevTools → Application → IndexedDB.

In the app: **Settings → Run storage probe** writes a timestamp; **Reset proof store** clears the store.

Programmatic reset is `resetStore()` in `src/storage.ts`.

This database is not a weather cache.

## Browser limitations discovered

- `npm run dev` does not represent production service-worker behavior. Always verify offline shell on `preview`.
- Hash routing (`#/today`) avoids server rewrite configuration for GitHub Pages-style static hosts.
- Standalone install and `beforeinstallprompt` availability vary by Android browser. Vanadium / Chromium is the intended path.
- System fonts differ across GrapheneOS and stock Android; tabular numbers depend on the UI face.
- `apple-touch-icon` is present for completeness; iOS is not a v1 target.
