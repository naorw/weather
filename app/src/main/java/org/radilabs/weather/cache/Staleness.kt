package org.radilabs.weather.cache

const val FRESH_MS = 30 * 60 * 1000L

enum class Freshness {
    Live,
    Cached,
    Stale,
    Missing,
}

fun classifyFreshness(fetchedAtMs: Long?, nowMs: Long, live: Boolean): Freshness {
    if (live) return Freshness.Live
    if (fetchedAtMs == null) return Freshness.Missing
    val age = nowMs - fetchedAtMs
    return if (age < FRESH_MS) Freshness.Cached else Freshness.Stale
}

fun formatAge(fetchedAtMs: Long, nowMs: Long): String {
    val age = (nowMs - fetchedAtMs).coerceAtLeast(0)
    val minutes = age / 60_000L
    val hours = age / 3_600_000L
    val days = age / 86_400_000L
    return when {
        minutes < 1 -> "JUST NOW"
        minutes < 60 -> "$minutes MIN AGO"
        hours < 24 -> "$hours H AGO"
        else -> "$days D AGO"
    }
}

fun freshnessLabel(freshness: Freshness, fetchedAtMs: Long?, nowMs: Long): String? {
    val age = fetchedAtMs?.let { formatAge(it, nowMs) } ?: return null
    return when (freshness) {
        Freshness.Cached -> "CACHED · UPDATED $age"
        Freshness.Stale -> "STALE · UPDATED $age"
        Freshness.Live, Freshness.Missing -> null
    }
}
