package org.radilabs.weather.ui.today

import org.radilabs.weather.weather.AirQuality
import org.radilabs.weather.weather.ConditionCategory
import org.radilabs.weather.weather.DailySummary
import org.radilabs.weather.weather.ForecastPoint
import org.radilabs.weather.weather.WeatherSnapshot
import org.radilabs.weather.weather.Wind
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale
import kotlin.math.roundToInt

fun presentSnapshot(snapshot: WeatherSnapshot): TodaySnapshot {
    val current = snapshot.current
    val todayKey = localDateFor(current.observedAtMs, snapshot.location.timezoneOffsetSeconds)
    val todaySummary = snapshot.days.find { it.localDate == todayKey }
    val high = todaySummary?.highC ?: current.highC
    val low = todaySummary?.lowC ?: current.lowC
    val country = snapshot.location.country?.let { " $it" }.orEmpty()
    return TodaySnapshot(
        location = "${snapshot.location.displayName}$country".trim(),
        temperatureC = current.temperatureC.roundToInt(),
        condition = current.conditionText.replaceFirstChar { it.uppercase() },
        conditionId = current.condition.toGlyph(),
        highC = high?.roundToInt(),
        lowC = low?.roundToInt(),
        feelsLikeC = current.feelsLikeC.roundToInt(),
        updatedAt = formatUpdated(current.observedAtMs, snapshot.location.timezoneOffsetSeconds),
        humidity = current.humidityPercent?.let { "${it.roundToInt()}%" } ?: "—",
        pressure = current.pressureHpa?.let { "${it.roundToInt()} hPa" } ?: "—",
        hours = snapshot.points.take(8).map { it.toHour(snapshot.location.timezoneOffsetSeconds) },
        days = snapshot.days.map { it.toDay() },
        wind = formatWind(current.wind),
        windDeg = current.wind.directionDeg,
        precipitation = formatPrecip(current.precipitation.rainMm, current.precipitation.snowMm),
        visibility = formatVisibility(current.visibilityM),
        airQuality = snapshot.airQuality?.toView(),
    )
}

private fun ForecastPoint.toHour(offsetSeconds: Int): HourPoint {
    val local = Instant.ofEpochMilli(atMs).atOffset(ZoneOffset.ofTotalSeconds(offsetSeconds))
    return HourPoint(
        hour = "%02d".format(local.hour),
        tempC = temperatureC.roundToInt(),
        condition = condition.toGlyph(),
        precipChance = precipitation.probabilityPercent?.roundToInt(),
    )
}

private fun DailySummary.toDay(): DayPoint {
    val date = LocalDate.parse(localDate)
    return DayPoint(
        day = date.dayOfWeek.short(),
        date = "%02d".format(date.dayOfMonth),
        condition = condition.toGlyph(),
        precipChance = precipitation.probabilityPercent?.roundToInt(),
        lowC = lowC.roundToInt(),
        highC = highC.roundToInt(),
        partial = partial,
    )
}

private fun DayOfWeek.short(): String {
    return name.take(3).lowercase(Locale.US).replaceFirstChar { it.uppercase() }
}

private fun localDateFor(atMs: Long, offsetSeconds: Int): String {
    return Instant.ofEpochMilli(atMs).atOffset(ZoneOffset.ofTotalSeconds(offsetSeconds)).toLocalDate().toString()
}

private fun formatUpdated(atMs: Long, offsetSeconds: Int): String {
    val local = Instant.ofEpochMilli(atMs).atOffset(ZoneOffset.ofTotalSeconds(offsetSeconds))
    return "%02d:%02d".format(local.hour, local.minute)
}

internal fun formatWind(wind: Wind): String {
    val dir = wind.directionDeg?.let { cardinal(it) + " " }.orEmpty()
    val gust = wind.gustMps?.let { "  G ${it.roundToInt()}" }.orEmpty()
    return dir + "${wind.speedMps.roundToInt()} m/s$gust"
}

internal fun cardinal(deg: Double): String {
    val names = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = ((deg / 22.5) + 0.5).toInt().mod(16)
    return names[index]
}

internal fun formatPrecip(rainMm: Double?, snowMm: Double?): String {
    return when {
        rainMm != null && snowMm != null -> "${formatMm(rainMm)} rain / ${formatMm(snowMm)} snow"
        rainMm != null -> formatMm(rainMm)
        snowMm != null -> "${formatMm(snowMm)} snow"
        else -> "—"
    }
}

private fun formatMm(mm: Double): String {
    val text = if (mm >= 10) mm.roundToInt().toString() else "%.1f".format(Locale.US, mm)
    return "$text mm"
}

internal fun formatVisibility(metres: Double?): String {
    if (metres == null) return "—"
    return if (metres >= 1000) "${(metres / 1000.0).roundToInt()} km" else "${metres.roundToInt()} m"
}

private fun AirQuality.toView(): AirQualityView {
    return AirQualityView(index = openWeatherAqi, category = airQualityLabel(category))
}

fun ConditionCategory.toGlyph(): GlyphId {
    return when (this) {
        ConditionCategory.Clear -> GlyphId.Clear
        ConditionCategory.PartlyCloudy -> GlyphId.PartlyCloudy
        ConditionCategory.Cloudy -> GlyphId.Cloudy
        ConditionCategory.Overcast -> GlyphId.Overcast
        ConditionCategory.Drizzle -> GlyphId.Drizzle
        ConditionCategory.LightRain -> GlyphId.LightRain
        ConditionCategory.Rain -> GlyphId.Rain
        ConditionCategory.HeavyRain -> GlyphId.HeavyRain
        ConditionCategory.Thunderstorm -> GlyphId.Thunderstorm
        ConditionCategory.LightSnow -> GlyphId.LightSnow
        ConditionCategory.Snow -> GlyphId.Snow
        ConditionCategory.Fog -> GlyphId.Fog
        ConditionCategory.Unknown -> GlyphId.Unknown
    }
}
