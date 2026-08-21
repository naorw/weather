package org.radilabs.weather.weather

const val COMPLETE_DAY_POINTS = 8

fun aggregateDaily(points: List<ForecastPoint>, timezoneOffsetSeconds: Int): List<DailySummary> {
    val groups = linkedMapOf<String, MutableList<ForecastPoint>>()
    for (point in points) {
        val key = localDateKey(point.atMs, timezoneOffsetSeconds)
        groups.getOrPut(key) { mutableListOf() }.add(point)
    }
    return groups.entries
        .sortedBy { it.key }
        .map { (localDate, dayPoints) ->
            DailySummary(
                localDate = localDate,
                highC = dayPoints.maxOf { it.temperatureC },
                lowC = dayPoints.minOf { it.temperatureC },
                condition = pickRepresentativeCondition(dayPoints.map { it.condition }),
                precipitation = summarizePrecipitation(dayPoints.map { it.precipitation }),
                pointCount = dayPoints.size,
                partial = dayPoints.size < COMPLETE_DAY_POINTS,
            )
        }
}

private fun summarizePrecipitation(items: List<Precipitation>): Precipitation {
    val probs = items.mapNotNull { it.probabilityPercent }
    val rains = items.mapNotNull { it.rainMm }
    val snows = items.mapNotNull { it.snowMm }
    return Precipitation(
        probabilityPercent = probs.maxOrNull(),
        rainMm = if (rains.isEmpty()) null else rains.sum(),
        snowMm = if (snows.isEmpty()) null else snows.sum(),
    )
}
