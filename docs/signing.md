# Release signing

v0.1.0 must be signed with a **production** key, not the Android debug key.

The keystore, passwords, and alias stay **off Git**. Never commit them. Never put them in `reviews/`, docs examples, or logs.

## One-time owner setup

On the machine that will build releases:

```sh
mkdir -p "$HOME/.local/weather-signing"
keytool -genkeypair -v \
  -keystore "$HOME/.local/weather-signing/weather-release.jks" \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias weather
```

`keytool` will ask for a store password, key password, and certificate identity. Remember them. Losing this keystore means future upgrades cannot replace the installed production app.

Add to **local** `local.properties` (already gitignored):

```
weather.release.storeFile=/home/YOU/.local/weather-signing/weather-release.jks
weather.release.storePassword=...
weather.release.keyAlias=weather
weather.release.keyPassword=...
```

Equivalent environment variables:

`WEATHER_RELEASE_STORE_FILE`, `WEATHER_RELEASE_STORE_PASSWORD`, `WEATHER_RELEASE_KEY_ALIAS`, `WEATHER_RELEASE_KEY_PASSWORD`

## Build

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
./gradlew :app:prepareReleaseArtifact
```

Outputs (gitignored):

* `dist/weather-v0.1.0.apk`
* `dist/SHA256SUMS`

`assembleRelease` **fails** if signing is not configured. That is intentional. Do not fall back to the debug key.

## First install after debug-signed Phase 0–4 builds

Every accepted build through Phase 4 used the **debug** keystore.

A production-signed `v0.1.0` APK **cannot** in-place upgrade that install.

1. Note that uninstalling Weather deletes the local OpenWeather key, saved cities, and weather cache.
2. `adb uninstall org.radilabs.weather`
3. `adb install dist/weather-v0.1.0.apk`

Later 0.1.x / 0.2.x releases **must reuse this same production key** so normal upgrades work.
