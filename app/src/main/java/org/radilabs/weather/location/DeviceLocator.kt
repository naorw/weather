package org.radilabs.weather.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import org.radilabs.weather.weather.Coordinates
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

class LocationUnavailableException(message: String) : Exception(message)

class DeviceLocator(private val context: Context) {
    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun currentCoordinates(): Coordinates {
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
        val location = oneShot(manager, provider) ?: manager.getLastKnownLocation(provider)
            ?: throw LocationUnavailableException("Location is unavailable.")
        return Coordinates(location.latitude, location.longitude)
    }

    private fun oneShot(manager: LocationManager, provider: String): Location? {
        val latch = CountDownLatch(1)
        val holder = AtomicReference<Location?>()
        if (Build.VERSION.SDK_INT >= 30) {
            val consumer = Consumer<Location?> { value ->
                holder.set(value)
                latch.countDown()
            }
            manager.getCurrentLocation(provider, CancellationSignal(), context.mainExecutor, consumer)
        } else {
            @Suppress("DEPRECATION")
            manager.requestSingleUpdate(provider, { value ->
                holder.set(value)
                latch.countDown()
            }, context.mainLooper)
        }
        latch.await(12, TimeUnit.SECONDS)
        return holder.get()
    }
}
