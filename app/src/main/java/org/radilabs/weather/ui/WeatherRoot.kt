package org.radilabs.weather.ui

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.radilabs.weather.location.DeviceLocator
import org.radilabs.weather.location.LocationUnavailableException
import org.radilabs.weather.persist.ApiKeyStore
import org.radilabs.weather.places.Place
import org.radilabs.weather.places.PlaceSource
import org.radilabs.weather.places.placeFromCoordinates
import org.radilabs.weather.session.SessionView
import org.radilabs.weather.session.WeatherSession
import org.radilabs.weather.ui.cities.CitiesScreen
import org.radilabs.weather.ui.radar.RadarScreen
import org.radilabs.weather.ui.settings.SettingsScreen
import org.radilabs.weather.ui.theme.Wx
import org.radilabs.weather.ui.today.TodayScreen
import org.radilabs.weather.ui.today.TodayUiState
import org.radilabs.weather.weather.WeatherError

@Composable
fun WeatherRoot(
    apiKeyStore: ApiKeyStore,
    session: WeatherSession,
    locator: DeviceLocator,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dest by remember { mutableStateOf(Dest.Today) }
    var today by remember { mutableStateOf<TodayUiState>(TodayUiState.Loading) }
    var saved by remember { mutableStateOf(session.saved()) }
    var active by remember { mutableStateOf(session.active()) }
    var results by remember { mutableStateOf<List<Place>>(emptyList()) }
    var citiesStatus by remember { mutableStateOf("") }
    var permissionDenied by remember { mutableStateOf(false) }

    fun publish(view: SessionView) {
        today = view.toUi()
        active = session.active()
        saved = session.saved()
    }

    fun refresh(place: Place = session.active()) {
        if (session.shouldReuseInFlight(place.cacheKey) && session.active().cacheKey == place.cacheKey) {
            publish(session.viewFromCache(place, acquiring = true))
            return
        }
        val gen = session.beginGeneration(place)
        publish(session.viewFromCache(place, acquiring = true))
        scope.launch {
            val outcome = withContext(Dispatchers.IO) {
                try {
                    Result.success(session.fetchSnapshot(place))
                } catch (error: WeatherError) {
                    Result.failure(error)
                } catch (error: Exception) {
                    Result.failure(WeatherError(WeatherError.Code.Unknown, "Weather request failed.", cause = error))
                }
            }
            val weather = outcome.getOrNull()
            val view = if (weather != null) {
                session.applySuccess(gen, place, weather)
            } else {
                session.applyFailure(gen, place, outcome.exceptionOrNull() as WeatherError)
            }
            if (view != null) publish(view)
        }
    }

    fun select(place: Place, save: Boolean) {
        val chosen = if (save) session.save(place) else session.activate(place)
        results = emptyList()
        citiesStatus = ""
        refresh(chosen)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val granted = grants.values.any { it }
        permissionDenied = !granted
        if (granted) {
            scope.launch { useDevice(session, locator, ::select, { citiesStatus = it }) }
        } else {
            citiesStatus = "Permission denied. Search still works."
        }
    }

    LaunchedEffect(Unit) { refresh(session.active()) }
    LaunchedEffect(dest) {
        if (dest == Dest.Today) refresh(session.active())
    }

    DisposableEffect(context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                refresh(session.active())
            }
        }
        runCatching { cm.registerDefaultNetworkCallback(callback) }
        onDispose { runCatching { cm.unregisterNetworkCallback(callback) } }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Wx.base)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Wx.space4, vertical = Wx.space3),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Wx.space3),
        ) {
            Text("WX", color = Wx.accent, fontSize = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 3.sp)
            Text("WEATHER", color = Wx.text, fontSize = 14.sp, letterSpacing = 4.sp)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(Wx.hairline)
                .background(Wx.border),
        )
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (dest) {
                Dest.Today -> TodayScreen(
                    state = today,
                    onRefresh = { refresh(session.active()) },
                    footer = "WX / ${active.displayName.uppercase()} / 3-HOUR FREE FORECAST",
                )
                Dest.Radar -> RadarScreen(
                    place = active,
                    apiKey = apiKeyStore.read(),
                )
                Dest.Cities -> CitiesScreen(
                    saved = saved,
                    active = active,
                    results = results,
                    status = citiesStatus,
                    permissionDenied = permissionDenied,
                    onSearch = { query ->
                        scope.launch {
                            try {
                                val hits = withContext(Dispatchers.IO) { session.search(query) }
                                results = hits
                                citiesStatus = if (hits.isEmpty()) "No results." else ""
                            } catch (error: WeatherError) {
                                results = emptyList()
                                citiesStatus = "${error.title}: ${error.message}"
                            }
                        }
                    },
                    onSelectResult = { select(it, save = true) },
                    onActivate = { select(it, save = false) },
                    onRemove = { place ->
                        val next = session.remove(place.cacheKey)
                        saved = session.saved()
                        active = next
                        refresh(next)
                    },
                    onSaveActive = { select(session.active(), save = true) },
                    onUseDevice = {
                        if (locator.hasPermission()) {
                            scope.launch { useDevice(session, locator, ::select, { citiesStatus = it }) }
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        }
                    },
                )
                Dest.Settings -> SettingsScreen(apiKeyStore)
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(Wx.hairline)
                .background(Wx.border),
        )
        Row(
            Modifier
                .fillMaxWidth()
                .background(Wx.surface)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(Wx.touchMin),
        ) {
            Dest.entries.forEach { item ->
                val isActive = dest == item
                Box(
                    Modifier
                        .weight(1f)
                        .height(Wx.touchMin)
                        .clickable { dest = item }
                        .then(if (isActive) Modifier.border(Wx.hairline, Wx.accent) else Modifier),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        item.label.uppercase(),
                        color = if (isActive) Wx.accent else Wx.textMuted,
                        fontSize = Wx.nav,
                        letterSpacing = 1.5.sp,
                    )
                }
            }
        }
    }
}

private fun SessionView.toUi(): TodayUiState {
    val snap = snapshot
    val err = error
    return when {
        snap != null -> TodayUiState.Ready(snap, acquiring, note, statusLine)
        err != null -> TodayUiState.Failed(err)
        else -> TodayUiState.Loading
    }
}

private suspend fun useDevice(
    session: WeatherSession,
    locator: DeviceLocator,
    select: (Place, Boolean) -> Unit,
    status: (String) -> Unit,
) {
    try {
        val coords = withContext(Dispatchers.IO) { locator.currentCoordinates() }
        val fallback = placeFromCoordinates(
            latitude = coords.latitude,
            longitude = coords.longitude,
            displayName = "Device location",
            source = PlaceSource.Device,
        )
        val named = withContext(Dispatchers.IO) { session.reverse(fallback) }
            .copy(source = PlaceSource.Device)
        select(named, false)
        status("")
    } catch (_: LocationUnavailableException) {
        status("Location unavailable. Search still works.")
    } catch (error: WeatherError) {
        status("${error.title}: ${error.message}")
    }
}
