# Offline and recovery

## Expected PWA behavior

- Application shell (top bar, nav, hash routes) comes from the service worker precache on a production/preview build.
- Active location resolves from IndexedDB even when the network is down.
- If that location has a cached snapshot, Today renders it with CACHED/STALE and age.
- If there is no cache, Today shows the existing empty/error offline state (`Offline` / no usable payload). It does not invent zeros.
- Failed refresh does not erase cache. There is no request polling loop.

## Recovery (no background sync)

Fresh data is requested only:

1. Manual **Refresh** on Today
2. Browser `online` event (one in-flight request per generation; duplicate events reuse it)
3. Today becoming the active screen (`load` if idle, otherwise `refresh`)

There is no timer/periodic scheduler and no Background Sync API.

## Known browser limitations

- `npm run dev` does not represent production offline. Use `npm run build && npm run preview` (or the installed PWA) for shell-offline checks.
- Hash routes (`#/today`) keep navigation working without server rewrites.
- Firefox: `localhost` vs LAN IP are different origins (service worker + IndexedDB). Unregister both if caches look empty.
- Vanadium/Chrome standalone: offline launch depends on a successful prior visit so Workbox could precache the shell.
- OpenWeather CORS still applies when online; the Vite `/ow` proxy is only for local hosts. Production calls OpenWeather directly.

## Testing procedure

Automated: Today controller cache-first load, failed refresh preservation, per-location isolation, reconnect success (see `tests/today/controller.test.ts`).

Manual (owner device):

1. Online: search/save a city, confirm Today loads.
2. Airplane mode / disable network: relaunch PWA, confirm shell + cached Today + STALE/CACHED age.
3. Refresh while offline: values remain, error note, cache intact.
4. Restore network: Refresh or wait for `online`; STALE clears only after success.
5. Confirm standalone display and that location permission was not requested on launch.
