package org.radilabs.weather.places

import org.radilabs.weather.weather.Coordinates
import org.radilabs.weather.weather.STOCKHOLM
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

enum class PlaceSource {
    Saved,
    Search,
    Device,
    Default,
}

data class Place(
    val cacheKey: String,
    val displayName: String,
    val coordinates: Coordinates,
    val country: String? = null,
    val region: String? = null,
    val source: PlaceSource,
) {
    val id: String get() = cacheKey

    fun contextLabel(): String {
        return listOfNotNull(region, country).joinToString(", ").ifBlank { coordinatesLabel() }
    }

    fun coordinatesLabel(): String {
        return String.format(Locale.US, "%.4f, %.4f", coordinates.latitude, coordinates.longitude)
    }
}

fun locationCacheKey(latitude: Double, longitude: Double): String {
    return "${round4(latitude)}:${round4(longitude)}"
}

fun round4(value: Double): String {
    return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).toPlainString()
}

fun roundedCoordinates(latitude: Double, longitude: Double): Coordinates {
    return Coordinates(
        latitude = round4(latitude).toDouble(),
        longitude = round4(longitude).toDouble(),
    )
}

fun placeFromCoordinates(
    latitude: Double,
    longitude: Double,
    displayName: String,
    country: String? = null,
    region: String? = null,
    source: PlaceSource,
): Place {
    val coords = roundedCoordinates(latitude, longitude)
    return Place(
        cacheKey = locationCacheKey(coords.latitude, coords.longitude),
        displayName = displayName,
        coordinates = coords,
        country = country,
        region = region,
        source = source,
    )
}

val STOCKHOLM_PLACE: Place = placeFromCoordinates(
    latitude = STOCKHOLM.latitude,
    longitude = STOCKHOLM.longitude,
    displayName = "Stockholm",
    country = "SE",
    source = PlaceSource.Default,
)
