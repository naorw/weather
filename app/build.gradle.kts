plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

import java.io.File
import java.util.Properties

fun localProp(name: String): String? {
    val fromEnv = System.getenv(name.replace('.', '_').uppercase())
    if (!fromEnv.isNullOrBlank()) return fromEnv
    val propsFile = rootProject.file("local.properties")
    if (!propsFile.exists()) return null
    val props = Properties()
    propsFile.inputStream().use { props.load(it) }
    return props.getProperty(name)?.takeIf { it.isNotBlank() }
}

val releaseStorePath = localProp("weather.release.storeFile") ?: System.getenv("WEATHER_RELEASE_STORE_FILE")
val releaseStorePassword = localProp("weather.release.storePassword") ?: System.getenv("WEATHER_RELEASE_STORE_PASSWORD")
val releaseKeyAlias = localProp("weather.release.keyAlias") ?: System.getenv("WEATHER_RELEASE_KEY_ALIAS")
val releaseKeyPassword = localProp("weather.release.keyPassword") ?: System.getenv("WEATHER_RELEASE_KEY_PASSWORD")
val hasReleaseSigning = listOf(releaseStorePath, releaseStorePassword, releaseKeyAlias, releaseKeyPassword).all { !it.isNullOrBlank() } &&
    releaseStorePath?.let { file(it).isFile } == true

android {
    namespace = "org.radilabs.weather"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.radilabs.weather"
        minSdk = 29
        targetSdk = 35
        versionCode = 6
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStorePath as String)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = if (hasReleaseSigning) {
                signingConfigs.getByName("release")
            } else {
                null
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

gradle.taskGraph.whenReady {
    val wantsRelease = allTasks.any {
        it.name == "assembleRelease" || it.name == "packageRelease" || it.name == "prepareReleaseArtifact"
    }
    if (wantsRelease && !hasReleaseSigning) {
        throw GradleException(
            "Release signing is not configured. Add weather.release.* to local.properties " +
                "(see docs/signing.md). Refusing to produce a debug-signed release APK.",
        )
    }
}

tasks.register("prepareReleaseArtifact") {
    dependsOn("assembleRelease")
    doLast {
        val apk = file("build/outputs/apk/release/app-release.apk")
        val dist = rootProject.file("dist")
        dist.mkdirs()
        val named = File(dist, "weather-v0.1.0.apk")
        apk.copyTo(named, overwrite = true)
        val sha = providers.exec {
            commandLine("sha256sum", named.absolutePath)
        }.standardOutput.asText.get().trim()
        File(dist, "SHA256SUMS").writeText("$sha\n")
        println(sha)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.08.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.maplibre.gl:android-sdk:11.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.json:json:20240303")
}
