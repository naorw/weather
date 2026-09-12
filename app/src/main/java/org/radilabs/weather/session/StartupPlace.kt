package org.radilabs.weather.session

import kotlinx.coroutines.CancellationException
import org.radilabs.weather.location.LocationUnavailableException
import org.radilabs.weather.places.Place
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.placeFromCoordinates
import org.radilabs.weather.weather.Coordinates

data class StartupPlaceResult(
    val place: Place,
    val usedDevice: Boolean,
)

/**
 * Process-scoped startup/manual-choice rules.
 * Survives Activity recreation; resets only when the process dies.
 */
object SessionPlacePolicy {
    private const val IDLE = 0
    private const val IN_PROGRESS = 1
    private const val DONE = 2

    @Volatile
    private var coldStartState = IDLE

    @Volatile
    private var startGeneration = 0

    @Volatile
    private var manualChoice = false

    /** Null means this process already finished cold-start place resolution. */
    fun tryBeginColdStart(): Int? {
        synchronized(this) {
            if (coldStartState == DONE) return null
            coldStartState = IN_PROGRESS
            startGeneration += 1
            return startGeneration
        }
    }

    fun completeColdStart(generation: Int) {
        synchronized(this) {
            if (coldStartState == IN_PROGRESS && startGeneration == generation) {
                coldStartState = DONE
            }
        }
    }

    fun abortColdStart(generation: Int) {
        synchronized(this) {
            if (coldStartState == IN_PROGRESS && startGeneration == generation) {
                coldStartState = IDLE
            }
        }
    }

    fun markManualChoice() {
        manualChoice = true
    }

    fun hasManualChoice(): Boolean = manualChoice

    fun resetForTests() {
        synchronized(this) {
            coldStartState = IDLE
            startGeneration = 0
            manualChoice = false
        }
    }
}

/**
 * Cold-start place selection. Permission is never requested here.
 * Device lookup is best-effort; failures keep [previous] and do not save.
 */
suspend fun resolveStartupPlace(
    hasPermission: Boolean,
    previous: Place,
    currentCoordinates: suspend () -> Coordinates,
    reverse: suspend (Place) -> Place,
    activate: (Place) -> Place,
): StartupPlaceResult {
    if (!hasPermission) {
        return StartupPlaceResult(previous, usedDevice = false)
    }
    return try {
        val coords = currentCoordinates()
        val hint = placeFromCoordinates(
            latitude = coords.latitude,
            longitude = coords.longitude,
            displayName = "Device location",
            source = PlaceSource.Device,
        )
        val named = reverse(hint)
        if (!isResolvedGeocode(hint, named)) {
            return StartupPlaceResult(fallbackPlace(previous, activate), usedDevice = false)
        }
        StartupPlaceResult(activate(named.copy(source = PlaceSource.Device)), usedDevice = true)
    } catch (_: LocationUnavailableException) {
        StartupPlaceResult(fallbackPlace(previous, activate), usedDevice = false)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        StartupPlaceResult(fallbackPlace(previous, activate), usedDevice = false)
    }
}

private fun isResolvedGeocode(hint: Place, named: Place): Boolean {
    val name = named.displayName.trim()
    return name.isNotEmpty() && name != hint.displayName
}

private fun fallbackPlace(previous: Place, activate: (Place) -> Place): Place {
    if (previous.source != PlaceSource.Device) return previous
    return activate(previous.copy(source = PlaceSource.Saved))
}
