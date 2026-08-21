# Development

Weather v1 is a **native Android** application (Kotlin, Jetpack Compose). Native source is not in the tree until Native Phase 0 is authorized.

A PWA prototype exists in Git history only. Do not revive Vite, service workers, or a WebView wrapper.

## Recommended history marker

If a tag is created later (owner authorization required):

`pwa-prototype-final`

Point it at the last PWA commit on `main` before the platform reset.

## Native work (not started)

When Native Phase 0 is authorized, this document should describe:

- Android Studio / SDK / JDK requirements
- debug install on Pixel / GrapheneOS
- release APK generation
- local API-key configuration

Until then, there is no `npm run dev` and no Gradle project.

## Durable references

- Product: `PROJECT.md`
- Native phases: `PHASES.md`
- Authorization: `TASKS.md`
- Design: `docs/design-system.md`
- OpenWeather: `docs/openweather.md`
- Prototype note: `docs/pwa-prototype.md`
