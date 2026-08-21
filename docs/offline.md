# Offline and recovery

The installed native application must launch without a network. Use local persistence, not a service worker or HTTPS origin.

## Expected behavior

- Active location resolves from local storage when the network is down.
- If that location has a cached snapshot, Today renders it with CACHED/STALE and age.
- If there is no cache, Today shows empty/error offline state. It does not invent zeros.
- Failed refresh does not erase cache. There is no request polling loop.

## Recovery (no background sync)

Fresh data is requested only by explicit user refresh and by a documented connectivity-return path (no timers, no WorkManager periodic sync unless a later phase contract says so).

Deduplicate in-flight work so a new location selection cannot be overwritten by an older request.

There is no Background Sync API and no service-worker shell.

## Testing procedure (native, when implemented)

1. Online: search/save a city, confirm Today loads.
2. Airplane mode: relaunch, confirm cached Today + STALE/CACHED age.
3. Refresh while offline: values remain, error note, cache intact.
4. Restore network: refresh; STALE clears only after success.
5. Confirm location permission was not requested on launch.
