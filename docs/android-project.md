# Android project

Single module `:app`. Package `org.radilabs.weather`. Version `0.0.0` / versionCode `1`.

```
app/src/main/java/org/radilabs/weather/
  MainActivity.kt
  persist/ApiKeyStore.kt
  ui/WeatherRoot.kt          # shell + bottom nav
  ui/Dest.kt
  ui/today/                  # static instrument
  ui/settings/               # runtime key
  ui/placeholder/
  ui/theme/                  # Wx tokens
```

Gradle:

- Android Gradle Plugin 8.13.2
- Kotlin 2.1.10
- compileSdk / targetSdk 35
- minSdk 29 (GrapheneOS / Pixel-era Android 10+)
- Compose BOM `2025.08.01` (pinned so compileSdk 35 / AGP 8.13 stay compatible)

No Hilt, no Navigation component, no network library, no INTERNET permission.

Release builds use the debug signing config until a production keystore exists.

See `docs/development.md` for commands.
