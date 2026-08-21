# Development

Weather v1 is a native Android application (Kotlin, Jetpack Compose).

The PWA prototype is in Git history only. Do not revive it.

## Prerequisites

- JDK 21 (this machine: `$HOME/.local/jdk-21`)
- Android SDK with platform 35 and build-tools 35.0.0 (this machine: `$HOME/.local/android-sdk`)
- `adb` from that SDK's `platform-tools`

Create `local.properties` (gitignored):

```
sdk.dir=/home/naorw/.local/android-sdk
```

Adjust `sdk.dir` if the SDK lives elsewhere.

```sh
export JAVA_HOME="$HOME/.local/jdk-21"
export ANDROID_HOME="$HOME/.local/android-sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
```

## Debug APK

```sh
./gradlew :app:assembleDebug
```

Output:

`app/build/outputs/apk/debug/app-debug.apk`

## Release-like APK

There is no production signing keystore yet.


`assembleRelease` produces an installable APK signed with the **debug keystore**. Treat it as a release-like artifact, not a store-ready signed build.

```sh
./gradlew :app:assembleRelease
```

Output:

`app/build/outputs/apk/release/app-release.apk`

## Tests

```sh
./gradlew :app:testDebugUnitTest
```

## Devices

```sh
adb devices
```

## Install / update

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Or the release-like APK:

```sh
adb install -r app/build/outputs/apk/release/app-release.apk
```

## Launch

```sh
adb shell am start -n org.radilabs.weather/.MainActivity
```

## Logs

```sh
adb logcat --pid="$(adb shell pidof -s org.radilabs.weather)"
```

Filter:

```sh
adb logcat | grep -i weather
```

Do not expect the OpenWeather key in logs. If it appears, that is a bug.

## Uninstall

```sh
adb uninstall org.radilabs.weather
```

## GrapheneOS / Pixel

Install the APK with `adb` (or copy the file to the device). Confirm:

1. App appears in the launcher as Weather
2. Launch does not require a browser
3. Today shows the graphite instrument
4. Bottom nav: Today / Radar / Cities / Settings
5. Settings can save a key; relaunch still shows configured; Remove clears it
