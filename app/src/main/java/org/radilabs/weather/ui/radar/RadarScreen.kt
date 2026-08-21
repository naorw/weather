package org.radilabs.weather.ui.radar

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.PropertyFactory.rasterOpacity
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.radilabs.weather.map.DEFAULT_MAP_ZOOM
import org.radilabs.weather.map.OPENFREE_DARK_STYLE
import org.radilabs.weather.map.OverlayPlan
import org.radilabs.weather.map.WeatherOverlay
import org.radilabs.weather.map.activePlaceGeoJson
import org.radilabs.weather.map.mapCameraTarget
import org.radilabs.weather.map.overlayNoteFor
import org.radilabs.weather.map.planOverlay
import org.radilabs.weather.places.Place
import org.radilabs.weather.ui.theme.Wx

private const val SOURCE_OVERLAY = "owm-overlay"
private const val LAYER_OVERLAY = "owm-overlay-layer"
private const val SOURCE_ACTIVE = "active-place"
private const val LAYER_ACTIVE_HALO = "active-halo"
private const val LAYER_ACTIVE_CORE = "active-core"

@Composable
fun RadarScreen(place: Place, apiKey: String?) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var overlay by remember { mutableStateOf(WeatherOverlay.Precipitation) }
    var mapError by remember { mutableStateOf<String?>(null) }
    var overlayNote by remember { mutableStateOf<String?>(null) }
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }

    val mapView = remember {
        runCatching {
            MapLibre.getInstance(context.applicationContext)
            MapView(context)
        }.onFailure {
            mapError = "Basemap failed to start."
        }.getOrNull()
    }

    DisposableEffect(mapView, lifecycleOwner) {
        val view = mapView ?: return@DisposableEffect onDispose { }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> view.onStart()
                Lifecycle.Event.ON_RESUME -> view.onResume()
                Lifecycle.Event.ON_PAUSE -> view.onPause()
                Lifecycle.Event.ON_STOP -> view.onStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        view.onCreate(Bundle())
        view.getMapAsync { map ->
            mapRef = map
            map.uiSettings.isCompassEnabled = false
            map.uiSettings.isRotateGesturesEnabled = false
            map.uiSettings.isAttributionEnabled = true
            val (lat, lon) = mapCameraTarget(place)
            map.cameraPosition = CameraPosition.Builder()
                .target(LatLng(lat, lon))
                .zoom(DEFAULT_MAP_ZOOM)
                .build()
            map.setStyle(Style.Builder().fromUri(OPENFREE_DARK_STYLE)) { style ->
                applyOverlay(style, overlay, apiKey) { overlayNote = it }
                ensureActiveMarker(style, place)
            }
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            runCatching {
                view.onPause()
                view.onStop()
                view.onDestroy()
            }
        }
    }

    LaunchedEffect(place.cacheKey, mapRef) {
        val map = mapRef ?: return@LaunchedEffect
        val (lat, lon) = mapCameraTarget(place)
        map.easeCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), DEFAULT_MAP_ZOOM))
        map.getStyle { style -> ensureActiveMarker(style, place) }
    }

    Column(Modifier.fillMaxSize().background(Wx.base)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Wx.space4, vertical = Wx.space2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("RADAR", color = Wx.text, fontSize = Wx.heading, letterSpacing = 2.sp)
                Text(
                    place.displayName.uppercase(),
                    color = Wx.accent,
                    fontSize = Wx.meta,
                    letterSpacing = 1.5.sp,
                )
            }
            Text(
                "RECENTER",
                color = Wx.accent,
                fontSize = Wx.nav,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .height(Wx.touchMin)
                    .clickable {
                        val (lat, lon) = mapCameraTarget(place)
                        mapRef?.easeCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), DEFAULT_MAP_ZOOM),
                        )
                    }
                    .padding(horizontal = Wx.space2, vertical = Wx.space3),
            )
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (mapView != null && mapError == null) {
                AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
            } else {
                Text(
                    mapError ?: "Basemap unavailable.",
                    color = Wx.warning,
                    fontSize = Wx.body,
                    modifier = Modifier.padding(Wx.space4),
                )
            }
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Wx.base.copy(alpha = 0.82f))
                    .padding(Wx.space3),
            ) {
                overlayNote?.let {
                    Text(it, color = Wx.amber, fontSize = Wx.meta, modifier = Modifier.padding(bottom = Wx.space2))
                }
                Text(overlay.legendTitle, color = Wx.textMuted, fontSize = Wx.meta, letterSpacing = 1.5.sp)
                Text(overlay.legendBody, color = Wx.text, fontSize = Wx.meta, modifier = Modifier.padding(top = Wx.space1))
                if (overlay == WeatherOverlay.Precipitation) {
                    PrecipLegend(Modifier.padding(top = Wx.space2))
                }
                Text(
                    place.coordinatesLabel(),
                    color = Wx.textMuted,
                    fontSize = Wx.meta,
                    modifier = Modifier.padding(top = Wx.space2),
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(Wx.touchMin)
                .background(Wx.surface),
        ) {
            WeatherOverlay.entries.forEach { item ->
                val selected = overlay == item
                Box(
                    Modifier
                        .weight(1f)
                        .height(Wx.touchMin)
                        .clickable {
                            overlay = item
                            mapRef?.getStyle { style ->
                                applyOverlay(style, item, apiKey) { overlayNote = it }
                                ensureActiveMarker(style, place)
                            }
                        }
                        .then(if (selected) Modifier.border(Wx.hairline, Wx.accent) else Modifier),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        item.controlLabel,
                        color = if (selected) Wx.accent else Wx.textMuted,
                        fontSize = Wx.nav,
                        letterSpacing = 1.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrecipLegend(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Wx.space2)) {
        LegendStop("NONE", Color(0xFF2E363F))
        LegendStop("LIGHT", Color(0xFF3D9A9A))
        LegendStop("MOD.", Color(0xFFC4923A))
        LegendStop("HEAVY", Color(0xFFC45C4A))
    }
}

@Composable
private fun LegendStop(label: String, color: Color) {
    Column {
        Box(Modifier.size(width = 36.dp, height = 6.dp).background(color))
        Text(label, color = Wx.textMuted, fontSize = 9.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

private fun applyOverlay(
    style: Style,
    overlay: WeatherOverlay,
    apiKey: String?,
    note: (String?) -> Unit,
) {
    runCatching { style.getLayer(LAYER_OVERLAY)?.let { style.removeLayer(it) } }
    runCatching { style.getSource(SOURCE_OVERLAY)?.let { style.removeSource(it) } }
    when (val plan = planOverlay(overlay, apiKey)) {
        OverlayPlan.Hidden, OverlayPlan.MissingKey -> {
            note(overlayNoteFor(plan))
        }
        is OverlayPlan.Tiles -> {
            try {
                val tiles = TileSet("2.1.0", plan.template)
                tiles.minZoom = 0f
                tiles.maxZoom = 18f
                style.addSource(RasterSource(SOURCE_OVERLAY, tiles, 256))
                style.addLayer(
                    RasterLayer(LAYER_OVERLAY, SOURCE_OVERLAY).withProperties(rasterOpacity(0.72f)),
                )
                note(null)
            } catch (_: Exception) {
                note("Overlay unavailable. Basemap still works.")
            }
        }
    }
}

private fun ensureActiveMarker(style: Style, place: Place) {
    val json = activePlaceGeoJson(place)
    val existing = style.getSourceAs<GeoJsonSource>(SOURCE_ACTIVE)
    if (existing != null) {
        existing.setGeoJson(json)
        runCatching { style.getLayer(LAYER_ACTIVE_HALO)?.let { style.removeLayer(it) } }
        runCatching { style.getLayer(LAYER_ACTIVE_CORE)?.let { style.removeLayer(it) } }
    } else {
        style.addSource(GeoJsonSource(SOURCE_ACTIVE, json))
    }
    style.addLayer(
        CircleLayer(LAYER_ACTIVE_HALO, SOURCE_ACTIVE).withProperties(
            circleRadius(14f),
            circleColor("#3D9A9A"),
            circleOpacity(0.28f),
        ),
    )
    style.addLayer(
        CircleLayer(LAYER_ACTIVE_CORE, SOURCE_ACTIVE).withProperties(
            circleRadius(5f),
            circleColor("#3D9A9A"),
            circleStrokeWidth(1.2f),
            circleStrokeColor("#E6E4DC"),
        ),
    )
}
