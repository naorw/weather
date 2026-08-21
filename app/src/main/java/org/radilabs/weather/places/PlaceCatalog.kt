package org.radilabs.weather.places

interface PlaceCatalog {
    fun active(): Place
    fun saved(): List<Place>
    fun save(place: Place): Place
    fun remove(cacheKey: String): Place
    fun setActive(place: Place): Place
}

class MemoryPlaceCatalog(
    initialActive: Place = STOCKHOLM_PLACE,
    initialSaved: List<Place> = emptyList(),
) : PlaceCatalog {
    private var activePlace: Place = initialActive
    private val savedPlaces = initialSaved.distinctBy { it.cacheKey }.toMutableList()

    override fun active(): Place = activePlace

    override fun saved(): List<Place> = savedPlaces.toList()

    override fun save(place: Place): Place {
        val stored = place.copy(source = PlaceSource.Saved)
        val index = savedPlaces.indexOfFirst { it.cacheKey == stored.cacheKey }
        if (index >= 0) {
            savedPlaces[index] = stored
        } else {
            savedPlaces.add(stored)
        }
        activePlace = stored
        return stored
    }

    override fun remove(cacheKey: String): Place {
        savedPlaces.removeAll { it.cacheKey == cacheKey }
        if (activePlace.cacheKey == cacheKey) {
            activePlace = savedPlaces.firstOrNull() ?: STOCKHOLM_PLACE
        }
        return activePlace
    }

    override fun setActive(place: Place): Place {
        val existing = savedPlaces.find { it.cacheKey == place.cacheKey }
        activePlace = existing ?: place
        return activePlace
    }
}
