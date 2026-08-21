# Geolocation

Device location is optional and user-initiated from Cities (**Use device location**).

## Permission flow

1. Never requested on startup or Today load.
2. Requested only after the Cities control is used.
3. Uses `navigator.geolocation.getCurrentPosition` once (`enableHighAccuracy: false`, `maximumAge: 0`, 12 s timeout).
4. `watchPosition` is not used. There is no location history store and no background tracking.

Returned coordinates are used for weather immediately. Reverse geocoding (`/geo/1.0/reverse`) is best-effort for a display name. If it fails, the label is `Device location`.

## States

| State | UI |
| --- | --- |
| granted | Activates coordinates; optional save from Cities |
| prompt / not yet decided | Button available |
| denied | Button disabled; copy says search still works. No repeat nag. |
| unsupported | Button disabled |
| timeout / unavailable | Status copy; button remains usable |
| insecure context | Button/path reports that HTTPS or localhost is required |

`navigator.permissions.query({ name: "geolocation" })` is used when present so a prior denial can disable the control on Cities hydrate.

## Browser / security-context constraints

- Geolocation requires a [secure context](https://developer.mozilla.org/en-US/docs/Web/Security/Secure_Contexts): HTTPS or `localhost`. `http://192.168.x.x` preview on a phone will typically be treated as insecure.
- GrapheneOS / Vanadium: site permission must be granted; the OS may also keep location disabled globally. Denial must not block search or saved cities.
- Firefox: permission UI differs; `permissions.query` may be missing or throw — treated as `unknown`.
- Standalone PWA uses the same origin permission as the installing browser.

Weather remains fully usable without this permission.
