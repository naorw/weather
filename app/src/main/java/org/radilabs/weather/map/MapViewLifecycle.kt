package org.radilabs.weather.map

/** MapLibre MapView must receive onCreate before lifecycle observers can dispatch onStart/onResume. */
fun runMapAttach(onCreate: () -> Unit, registerObserver: () -> Unit) {
    onCreate()
    registerObserver()
}
