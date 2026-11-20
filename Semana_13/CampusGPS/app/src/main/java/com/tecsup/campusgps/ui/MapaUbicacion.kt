package com.tecsup.campusgps.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.TECSUP_LIMA_LATITUD
import com.tecsup.campusgps.model.TECSUP_LIMA_LONGITUD
import com.tecsup.campusgps.model.UbicacionUi
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.fillOutlineColor
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import com.tecsup.campusgps.R

private const val OPEN_FREE_MAP_LIGHT = "https://tiles.openfreemap.org/styles/positron"
private const val OPEN_FREE_MAP_DARK = "https://tiles.openfreemap.org/styles/dark"

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercanoId: Int? = null,
    radioMetros: Int? = null,
    mostrarPuntoAzul: Boolean = false,
    mapaOscuro: Boolean = false,
    onSeleccionarLugar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleLoaded by remember { mutableStateOf(false) }

    mapView?.let { view ->
        DisposableEffect(lifecycleOwner, view) {
            val observer = object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) = view.onStart()
                override fun onResume(owner: LifecycleOwner) = view.onResume()
                override fun onPause(owner: LifecycleOwner) = view.onPause()
                override fun onStop(owner: LifecycleOwner) = view.onStop()
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                view.onDestroy()
                mapView = null
                map = null
            }
        }
    }

    LaunchedEffect(map, mapaOscuro) {
        val currentMap = map ?: return@LaunchedEffect
        styleLoaded = false
        currentMap.setStyle(if (mapaOscuro) OPEN_FREE_MAP_DARK else OPEN_FREE_MAP_LIGHT) {
            styleLoaded = true
        }
    }

    LaunchedEffect(
        map, styleLoaded, estado, lugares, seleccionado, masCercanoId, radioMetros,
        mostrarPuntoAzul, onSeleccionarLugar
    ) {
        val currentMap = map ?: return@LaunchedEffect
        if (!styleLoaded) return@LaunchedEffect

        currentMap.clear()
        val campus = LatLng(TECSUP_LIMA_LATITUD, TECSUP_LIMA_LONGITUD)
        currentMap.addMarker(
            MarkerOptions()
                .position(campus)
                .title("TECSUP · Sede Lima")
                .snippet("Av. Cascanueces 2221, Santa Anita")
                .icon(createMarkerIcon(
                    context,
                    if (seleccionado == 0) Color.rgb(255, 48, 75) else Color.rgb(105, 119, 124)
                ))
        )
        val style = currentMap.style
        if (style?.getLayer(ROUTE_LAYER) != null) style.removeLayer(ROUTE_LAYER)
        if (style?.getSource(ROUTE_SOURCE) != null) style.removeSource(ROUTE_SOURCE)
        if (style?.getLayer(RADIUS_LAYER) != null) style.removeLayer(RADIUS_LAYER)
        if (style?.getSource(RADIUS_SOURCE) != null) style.removeSource(RADIUS_SOURCE)
        val userPosition = (estado as? UbicacionUi.Lista)?.posicion
        val selectedPlace = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
        if (style != null && userPosition != null && selectedPlace != null) {
            val route = LineString.fromLngLats(
                listOf(
                    Point.fromLngLat(userPosition.longitud, userPosition.latitud),
                    Point.fromLngLat(selectedPlace.longitud, selectedPlace.latitud)
                )
            )
            style.addSource(GeoJsonSource(ROUTE_SOURCE, Feature.fromGeometry(route)))
            style.addLayer(
                LineLayer(ROUTE_LAYER, ROUTE_SOURCE).withProperties(
                    lineColor(Color.rgb(83, 219, 197)),
                    lineWidth(4f)
                )
            )
        }
        if (userPosition != null) {
            val point = LatLng(userPosition.latitud, userPosition.longitud)
            if (mostrarPuntoAzul) {
                currentMap.addMarker(
                    MarkerOptions()
                        .position(point)
                        .title("Estás aquí")
                        .icon(createMarkerIcon(context, Color.rgb(33, 150, 243)))
                )
            }
            radioMetros?.let { radius ->
                if (style != null) {
                    val geometry = circlePolygon(point, radius.toDouble())
                    style.addSource(
                        GeoJsonSource(
                            RADIUS_SOURCE,
                            FeatureCollection.fromFeature(Feature.fromGeometry(geometry))
                        )
                    )
                    style.addLayer(
                        FillLayer(RADIUS_LAYER, RADIUS_SOURCE).withProperties(
                            fillColor(Color.rgb(63, 81, 181)),
                            fillOpacity(0.16f),
                            fillOutlineColor(Color.rgb(63, 81, 181))
                        )
                    )
                }
            }
        }

        lugares.forEach { item ->
            val selected = item.lugar.id == seleccionado
            val nearest = item.lugar.id == masCercanoId
            currentMap.addMarker(
                MarkerOptions()
                    .position(LatLng(item.lugar.latitud, item.lugar.longitud))
                    .title(
                        when {
                            selected -> "Destino seleccionado · ${item.lugar.nombre}"
                            nearest -> "Más cercano · ${item.lugar.nombre}"
                            else -> item.lugar.nombre
                        }
                    )
                    .snippet(item.lugar.categoria)
                    .icon(createMarkerIcon(
                        context,
                        when {
                            selected -> Color.rgb(255, 48, 75)
                            nearest -> Color.rgb(149, 158, 160)
                            else -> Color.rgb(105, 119, 124)
                        }
                    ))
            )
        }

        currentMap.setOnMarkerClickListener { marker ->
            val lugarMarcado = lugares.firstOrNull {
                kotlin.math.abs(it.lugar.latitud - marker.position.latitude) < 0.000001 &&
                    kotlin.math.abs(it.lugar.longitud - marker.position.longitude) < 0.000001
            }
            when {
                lugarMarcado != null -> {
                    onSeleccionarLugar(lugarMarcado.lugar.id)
                    true
                }
                kotlin.math.abs(TECSUP_LIMA_LATITUD - marker.position.latitude) < 0.000001 &&
                    kotlin.math.abs(TECSUP_LIMA_LONGITUD - marker.position.longitude) < 0.000001 -> {
                    onSeleccionarLugar(0)
                    true
                }
                else -> false
            }
        }

        when {
            selectedPlace != null -> currentMap.animateCamera(
                // Mantiene contexto suficiente para distinguir los puntos cercanos del campus.
                CameraUpdateFactory.newLatLngZoom(LatLng(selectedPlace.latitud, selectedPlace.longitud), 16.0),
                700
            )
            seleccionado == 0 -> currentMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(campus, 16.0),
                700
            )
            userPosition != null -> currentMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(userPosition.latitud, userPosition.longitud),
                    16.0
                ),
                1000
            )
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView ?: MapView(it).also { created ->
                created.onCreate(null)
                mapView = created
                created.getMapAsync { readyMap ->
                    map = readyMap
                    readyMap.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(TECSUP_LIMA_LATITUD, TECSUP_LIMA_LONGITUD))
                        .zoom(15.0)
                        .build()
                }
            } }
        )
        if (mostrarPuntoAzul && estado is UbicacionUi.Lista) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                onClick = {
                    val point = estado.posicion
                    map?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(point.latitud, point.longitud), 16.0),
                        500
                    )
                }
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Volver a mi ubicación")
            }
        }
    }
}

private fun createMarkerIcon(context: android.content.Context, color: Int) =
    IconFactory.getInstance(context).fromBitmap(
        Bitmap.createBitmap(72, 72, Bitmap.Config.ARGB_8888).also { bitmap ->
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_map_marker)?.mutate()
                ?: return@also
            drawable.setTint(color)
            drawable.setBounds(0, 0, bitmap.width, bitmap.height)
            drawable.draw(Canvas(bitmap))
        }
    )

private const val RADIUS_SOURCE = "campus-radius-source"
private const val RADIUS_LAYER = "campus-radius-layer"
private const val ROUTE_SOURCE = "campus-route-source"
private const val ROUTE_LAYER = "campus-route-layer"

private fun circlePolygon(center: LatLng, radiusMeters: Double): Polygon {
    val earthRadius = 6378137.0
    val angularDistance = radiusMeters / earthRadius
    val centerLatitude = Math.toRadians(center.latitude)
    val centerLongitude = Math.toRadians(center.longitude)
    val points = (0..64).map { step ->
        val bearing = 2.0 * Math.PI * step / 64.0
        val latitude = kotlin.math.asin(
            kotlin.math.sin(centerLatitude) * kotlin.math.cos(angularDistance) +
                kotlin.math.cos(centerLatitude) * kotlin.math.sin(angularDistance) * kotlin.math.cos(bearing)
        )
        val longitude = centerLongitude + kotlin.math.atan2(
            kotlin.math.sin(bearing) * kotlin.math.sin(angularDistance) * kotlin.math.cos(centerLatitude),
            kotlin.math.cos(angularDistance) - kotlin.math.sin(centerLatitude) * kotlin.math.sin(latitude)
        )
        Point.fromLngLat(Math.toDegrees(longitude), Math.toDegrees(latitude))
    }
    return Polygon.fromLngLats(listOf(points))
}
