# Geolocation

Device location is optional and user-initiated from Cities.

## Permission flow

1. Never requested on startup or Today load.
2. Requested only after an explicit Cities action.
3. One-shot current location. Do not use continuous background tracking.
4. `watchPosition`-style tracking is not used. There is no location history store.

Returned coordinates are used for weather immediately after rounding to the location identity (`lat/lon` to 4 decimals). Reverse geocoding is best-effort for a display name. If it fails, the label is `Device location`.

## States

| State | UI |
| --- | --- |
| granted | Activates coordinates; optional save from Cities |
| not yet decided | Control available |
| denied | Control disabled or inert; copy says search still works. No repeat nag. |
| unavailable / timeout | Status copy; search still works |

GrapheneOS may keep location disabled globally. Denial must not block search or saved cities.

Weather remains fully usable without this permission.

Browser secure-context / `navigator.geolocation` rules applied only to the abandoned PWA prototype.
