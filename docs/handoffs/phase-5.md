# Phase 5 handoff (native Android)

Date: 2026-08-21

Accepted: 2026-08-21

Status: **accepted**

First public release **Weather v0.1.0** is authorized. Remaining non-blocking polish is deferred to **v0.1.1** or a later authorized phase. Do not begin Phase 6.

## Release candidate version

* `versionName` **0.1.0**
* `versionCode` **6**
* Settings shows runtime `PackageInfo` (`Weather 0.1.0 (6) · org.radilabs.weather`)

## Audit findings addressed

Blockers: B-1 version/dynamic Settings; B-2 MapLibre `onCreate` before observer; B-3 cache write never crashes (session + file write).

High: H-1 cache I/O on `Dispatchers.IO`; H-2 coroutine containment with cancellation rethrow; H-3 `toWeatherError()`; H-4 adaptive launcher + density buckets; H-5 production signing **path** (no debug-key fallback).

Medium taken: atomic cache write; cancellable location; exhaustive Today `when`; typed `Freshness`; dead `PlaceholderScreen` / `weatherQuery` / `usableSnapshot`; Settings copy; TalkBack labels; skip automatic refresh when cache is still CACHED; MapView `onLowMemory`.

## Findings deferred

* **H-6 R8/minify:** evaluated and **left off** for v0.1.0. Deferred to v0.1.1 or later if desired.
* Extra Maps 1.0 layers, observed radar, broader code audit, ABI splits.

## Lifecycle

Radar: `runMapAttach { onCreate }` then register lifecycle observer. `onLowMemory` / trim forwarded to `MapView`.

## Persistence / cache

Atomic temp+rename writes. Write failures are swallowed; in-memory LIVE snapshot remains. Corrupt rows still dropped on read. Cache read/write from refresh coroutines on IO.

## Accessibility

See `docs/accessibility.md`. Practical TalkBack/touch-target/contrast pass. Not WCAG certified.

## Network / performance

Startup still does one live fetch. Returning to Today or connectivity `onAvailable` skips a new fetch when the on-disk snapshot is still **CACHED** (&lt; 30 min). Manual Refresh always fetches. In-flight reuse unchanged. No polling / WorkManager.

## Release signing state

Production signing is configured locally via gitignored `local.properties`. Release APKs are production-signed. Debug-key fallback is refused.

First production install **cannot** overlay Phase 0–4 debug-signed apps. Uninstall wipes key/cities/cache. Future 0.1.x upgrades must reuse this production key.

## APK path

* Production (gitignored local copy): `dist/weather-v0.1.0.apk`
* Checksums: `dist/SHA256SUMS`
* Published on the GitHub Release for tag `v0.1.0`

## SHA256

`a0f72dc8d1de70ef023abdcb85e975fbf1bfd756597f9fa771849b48e97c10e7`  `weather-v0.1.0.apk`

Production signer certificate SHA-256: `3b1cc5f0dab2e0109298bb19543fd22550f46befa6df2e05bee071d454db06c4` (CN=Naor W, OU=Radi Labs, O=Forthscale). Not the Android debug certificate.

## Tests / results

```sh
./gradlew :app:testDebugUnitTest :app:prepareReleaseArtifact
```

**PASS** (2026-08-21). Production APK verifies (APK Signature Scheme v2). `reviews/` gitignored. No API key or signing secret in Git or the APK.

## Pixel / GrapheneOS owner validation

Owner accepted Native Phase 5 on Pixel / GrapheneOS (2026-08-21). Remaining non-blocking polish/testing is deferred to v0.1.1.

## GitHub Release

Published as **Weather v0.1.0** after this acceptance commit is tagged.

## Known limitations

* Maps 1.0 is not observed radar
* No offline map product
* R8 off (deferred)
* Uninstall required for debug→production signature change
* No background periodic sync, notifications, widgets, or accounts
