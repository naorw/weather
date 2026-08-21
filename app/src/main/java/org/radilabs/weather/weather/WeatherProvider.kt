package org.radilabs.weather.weather

interface WeatherProvider {
    fun getSnapshot(coordinates: Coordinates): WeatherSnapshot
}
