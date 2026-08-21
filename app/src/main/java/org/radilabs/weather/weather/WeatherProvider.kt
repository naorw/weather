package org.radilabs.weather.weather

import org.radilabs.weather.places.Place

interface WeatherProvider {
    fun getSnapshot(coordinates: Coordinates): WeatherSnapshot
    fun searchPlaces(query: String): List<Place>
    fun reverseGeocode(coordinates: Coordinates): Place
}
