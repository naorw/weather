# Android project

Single module `:app`. Package `org.radilabs.weather`. Version `0.3.0` / versionCode `4`.

```
app/src/main/java/org/radilabs/weather/
  MainActivity.kt
  persist/ApiKeyStore.kt
  weather/                   # application-owned models, errors, aggregation
  weather/openweather/       # HTTP + JSON mapping
  ui/WeatherRoot.kt
  ui/instrument/             # section label, hairline, range bar, compass, AQ scale
  ui/today/                  # Today instrument
  ui/settings/
```

Gradle:

- Android Gradle Plugin 8.13.2
- Kotlin 2.1.10
- compileSdk / targetSdk 35
- minSdk 29
- Compose BOM `2025.08.01`
- OkHttp 4.12.0

Permission: `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`. No background location. Location is requested only from Cities after an explicit action.

Release builds use the debug signing config until a production keystore exists.
