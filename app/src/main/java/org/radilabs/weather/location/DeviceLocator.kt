package org.radilabs.weather.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import org.radilabs.weather.weather.Coordinates
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationUnavailableException(message: String) : Exception(message)

class DeviceLocator(private val context: Context) {
    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    suspend fun currentCoordinates(): Coordinates {
        if (!hasPermission()) {
            throw LocationUnavailableException("Location permission denied.")
        }
        val manager = context.getSystemService(LocationManager::class.java)
            ?: throw LocationUnavailableException("Location is unavailable.")
        val provider = when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> throw LocationUnavailableException("Location is unavailable.")
        }
        val location = try {
            withTimeout(12_000) { oneShot(manager, provider) }
        } catch (_: TimeoutCancellationException) {
            manager.getLastKnownLocation(provider)
                ?: throw LocationUnavailableException("Location is unavailable.")
        } catch (cancelled: kotlinx.coroutines.CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            manager.getLastKnownLocation(provider)
                ?: throw LocationUnavailableException("Location is unavailable.")
        }
        return Coordinates(location.latitude, location.longitude)
    }

    private suspend fun oneShot(manager: LocationManager, provider: String): Location {
        return suspendCancellableCoroutine { cont ->
            val finished = AtomicBoolean(false)
            fun complete(location: Location?) {
                if (!finished.compareAndSet(false, true)) return
                if (location != null) {
                    cont.resume(location)
                } else {
                    cont.resumeWithException(LocationUnavailableException("Location is unavailable."))
                }
            }
            if (Build.VERSION.SDK_INT >= 30) {
                val signal = CancellationSignal()
                cont.invokeOnCancellation { signal.cancel() }
                manager.getCurrentLocation(provider, signal, context.mainExecutor) { value ->
                    complete(value)
                }
            } else {
                val listener = LocationListener { value -> complete(value) }
                cont.invokeOnCancellation {
                    runCatching { manager.removeUpdates(listener) }
                }
                @Suppress("DEPRECATION")
                manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            }
        }
    }
}
