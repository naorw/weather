package org.radilabs.weather.map

import org.radilabs.weather.places.Place
import org.radilabs.weather.weather.redactSecrets

enum class WeatherOverlay(
    val tileLayer: String?,
    val controlLabel: String,
    val legendTitle: String,
    val legendBody: String,
) {
    None(
        tileLayer = null,
        controlLabel = "NONE",
        legendTitle = "NO OVERLAY",
        legendBody = "Basemap only. OpenWeather map tiles are off.",
    ),
    Precipitation(
        tileLayer = "precipitation_new",
        controlLabel = "PRECIP MAP",
        legendTitle = "PRECIPITATION MAP",
        legendBody = "OpenWeather precipitation map (model field, about every 3 hours). Not observed radar. Stronger color means more precipitation.",
    ),
    Clouds(
        tileLayer = "clouds_new",
        controlLabel = "CLOUD COVER",
        legendTitle = "CLOUD COVER MAP",
        legendBody = "OpenWeather cloud-cover map. Not infrared satellite. Stronger white/grey means more cloud.",
    ),
}

const val OPENFREE_DARK_STYLE = "https://tiles.openfreemap.org/styles/dark"
const val OPENWEATHER_TILE_HOST = "https://tile.openweathermap.org/map"

const val DEFAULT_MAP_ZOOM = 7.0

fun overlayTileTemplate(overlay: WeatherOverlay, apiKey: String): String? {
    val layer = overlay.tileLayer ?: return null
    if (apiKey.isBlank()) return null
    return "$OPENWEATHER_TILE_HOST/$layer/{z}/{x}/{y}.png?appid=$apiKey"
}

fun overlayRequestPreview(overlay: WeatherOverlay, apiKey: String): String? {
    return overlayTileTemplate(overlay, apiKey)?.let(::redactSecrets)
}

fun mapCameraTarget(place: Place): Pair<Double, Double> {
    return place.coordinates.latitude to place.coordinates.longitude
}

fun activePlaceGeoJson(place: Place): String {
    val lon = place.coordinates.longitude
    val lat = place.coordinates.latitude
    return """{"type":"Feature","geometry":{"type":"Point","coordinates":[$lon,$lat]}}"""
}

fun overlayRequiresKey(overlay: WeatherOverlay): Boolean = overlay.tileLayer != null

sealed class OverlayPlan {
    data object Hidden : OverlayPlan()
    data object MissingKey : OverlayPlan()
    data class Tiles(val template: String) : OverlayPlan()
}

fun overlayNoteFor(plan: OverlayPlan): String? = when (plan) {
    OverlayPlan.Hidden -> null
    OverlayPlan.MissingKey -> "Credentials · overlay needs the OpenWeather key. Basemap still works."
    is OverlayPlan.Tiles -> null
}

fun planOverlay(overlay: WeatherOverlay, apiKey: String?): OverlayPlan {
    if (!overlayRequiresKey(overlay)) return OverlayPlan.Hidden
    val key = apiKey?.trim().orEmpty()
    if (key.isEmpty()) return OverlayPlan.MissingKey
    val template = overlayTileTemplate(overlay, key) ?: return OverlayPlan.MissingKey
    return OverlayPlan.Tiles(template)
}

