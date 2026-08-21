# Offline and recovery

The installed native application must launch without a network. Use local persistence, not a service worker or HTTPS origin.

## Expected behavior

- Active location resolves from local storage when the network is down.
- If that location has a cached snapshot, Today renders it with CACHED/STALE and age.
- If there is no cache, Today shows empty/error offline state. It does not invent zeros.
- Failed refresh does not erase cache. There is no request polling loop.

Radar is not an offline map product. Airplane mode must not break cached Today. MapLibre may show already-cached tiles if any; do not claim offline maps.

## Recovery (no background sync)

Fresh data is requested by explicit Today Refresh, returning to Today, and a `ConnectivityManager` default-network callback (no timers, no WorkManager). In-flight work is keyed by location and a generation token so a slower request for city A cannot paint city B.

There is no Background Sync API and no service-worker shell.

## Testing procedure (native, when implemented)

1. Online: search/save a city, confirm Today loads.
2. Airplane mode: relaunch, confirm cached Today + STALE/CACHED age.
3. Refresh while offline: values remain, error note, cache intact.
4. Restore network: refresh; STALE clears only after success.
5. Confirm location permission was not requested on launch.
