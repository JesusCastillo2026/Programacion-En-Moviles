package com.tecsup.campusgps.data

import android.net.Uri
import com.tecsup.campusgps.model.IndicacionRuta
import com.tecsup.campusgps.model.Posicion
import com.tecsup.campusgps.model.RutaPeatonal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface RutasRepository {
    suspend fun obtenerRuta(origen: Posicion, destino: Posicion): RutaPeatonal
}

class ValhallaRutasRepository : RutasRepository {
    override suspend fun obtenerRuta(origen: Posicion, destino: Posicion): RutaPeatonal =
        withContext(Dispatchers.IO) {
            require(CampusRutas.estaDentro(origen)) {
                "Tu ubicación está fuera del campus TECSUP. El recorrido interno estará disponible al ingresar."
            }
            require(CampusRutas.estaDentro(destino)) {
                "El destino seleccionado está fuera del campus TECSUP."
            }

            try {
                val rutaExterna = consultarValhalla(origen, destino)
                if (CampusRutas.rutaCompletaDentro(rutaExterna.puntos)) rutaExterna
                else CampusRutas.crearRutaInterna(origen, destino)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                CampusRutas.crearRutaInterna(origen, destino)
            }
        }

    private fun consultarValhalla(origen: Posicion, destino: Posicion): RutaPeatonal {
        val request = JSONObject()
            .put("locations", org.json.JSONArray().apply {
                put(JSONObject().put("lat", origen.latitud).put("lon", origen.longitud))
                put(JSONObject().put("lat", destino.latitud).put("lon", destino.longitud))
            })
            .put("costing", "pedestrian")
            .put("units", "kilometers")
            .put("language", "es-ES")
        val uri = Uri.parse("https://valhalla1.openstreetmap.de/route")
            .buildUpon().appendQueryParameter("json", request.toString()).build()
        val connection = URL(uri.toString()).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 8_000
            connection.readTimeout = 10_000
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode
            val body = (if (responseCode in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()
            check(responseCode in 200..299) { "El servicio de rutas respondió $responseCode." }
            val trip = JSONObject(body).getJSONObject("trip")
            val summary = trip.getJSONObject("summary")
            val leg = trip.getJSONArray("legs").getJSONObject(0)
            val points = decodePolyline6(leg.getString("shape"))
            val maneuvers = leg.getJSONArray("maneuvers")
            val directions = (0 until maneuvers.length()).mapNotNull { index ->
                val maneuver = maneuvers.getJSONObject(index)
                maneuver.optString("instruction")
                    .takeIf(String::isNotBlank)
                    ?.let { IndicacionRuta(it, maneuver.optDouble("length") * 1000.0, maneuver.optDouble("time")) }
            }
            check(points.size > 1 && directions.isNotEmpty()) { "No se encontró un recorrido peatonal." }
            RutaPeatonal(
                distanciaMetros = summary.getDouble("length") * 1000.0,
                duracionSegundos = summary.getDouble("time"),
                puntos = points,
                indicaciones = directions
            )
        } finally {
            connection.disconnect()
        }
    }

    private fun decodePolyline6(encoded: String): List<Posicion> {
        val points = mutableListOf<Posicion>()
        var index = 0
        var latitude = 0
        var longitude = 0
        fun readValue(): Int {
            var shift = 0
            var value = 0
            var chunk: Int
            do {
                check(index < encoded.length) { "La geometría de la ruta está incompleta." }
                chunk = encoded[index++].code - 63
                value = value or ((chunk and 0x1f) shl shift)
                shift += 5
            } while (chunk >= 0x20)
            return if (value and 1 == 1) (value shr 1).inv() else value shr 1
        }
        while (index < encoded.length) {
            latitude += readValue()
            longitude += readValue()
            points += Posicion(latitude / 1_000_000.0, longitude / 1_000_000.0)
        }
        return points
    }
}

private object CampusRutas {
    private const val EARTH_RADIUS_METERS = 6_371_000.0

    private val boundary = listOf(
        Posicion(-12.043136, -76.953772),
        Posicion(-12.043192, -76.951875),
        Posicion(-12.044115, -76.951610),
        Posicion(-12.044755, -76.951440),
        Posicion(-12.045078, -76.951703),
        Posicion(-12.045292, -76.952188),
        Posicion(-12.045308, -76.953341),
        Posicion(-12.045069, -76.953702),
        Posicion(-12.044403, -76.953888),
        Posicion(-12.043642, -76.953844)
    )

    private val pathNodes = listOf(
        Posicion(-12.043732, -76.953629), // Cancha de grass
        fromReferenceImage(205, 240),
        fromReferenceImage(235, 265),
        fromReferenceImage(260, 290),
        fromReferenceImage(285, 315),
        Posicion(-12.044376, -76.952827), // Salones de cómputo
        fromReferenceImage(320, 350),
        fromReferenceImage(330, 375),
        fromReferenceImage(345, 400),
        Posicion(-12.044828, -76.952715), // Auditorio
        fromReferenceImage(338, 445),
        fromReferenceImage(325, 458),
        Posicion(-12.045007, -76.952958), // Biblioteca
        fromReferenceImage(375, 440),
        fromReferenceImage(390, 450),
        Posicion(-12.045004, -76.952453), // Comedor
        fromReferenceImage(410, 435),
        fromReferenceImage(425, 422),
        fromReferenceImage(430, 415),
        fromReferenceImage(460, 407),
        fromReferenceImage(490, 400),
        fromReferenceImage(520, 390),
        fromReferenceImage(545, 378),
        Posicion(-12.044541, -76.951658), // Estacionamiento
        fromReferenceImage(545, 345),
        fromReferenceImage(525, 320),
        fromReferenceImage(505, 300),
        fromReferenceImage(485, 282),
        fromReferenceImage(465, 265),
        Posicion(-12.044004, -76.952229), // Cafetería
        fromReferenceImage(425, 280),
        fromReferenceImage(400, 298),
        fromReferenceImage(375, 315),
        fromReferenceImage(350, 325)
    )

    private val pathEdges = listOf(
        0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 5,
        5 to 6, 6 to 7, 7 to 8, 8 to 9,
        9 to 10, 10 to 11, 11 to 12,
        9 to 13, 13 to 14, 14 to 15,
        15 to 16, 16 to 17, 17 to 18, 18 to 19, 19 to 20,
        20 to 21, 21 to 22, 22 to 23,
        23 to 24, 24 to 25, 25 to 26, 26 to 27, 27 to 28, 28 to 29,
        29 to 30, 30 to 31, 31 to 32, 32 to 33, 33 to 5,
        5 to 8
    )

    fun estaDentro(point: Posicion): Boolean {
        var inside = false
        for (index in boundary.indices) {
            val next = (index + 1) % boundary.size
            val a = boundary[index]
            val b = boundary[next]
            val crosses = (a.latitud > point.latitud) != (b.latitud > point.latitud) &&
                point.longitud < (b.longitud - a.longitud) *
                (point.latitud - a.latitud) / (b.latitud - a.latitud) + a.longitud
            if (crosses) inside = !inside
        }
        return inside
    }

    fun rutaCompletaDentro(points: List<Posicion>): Boolean =
        points.size > 1 && points.all(::estaDentro) && points.zipWithNext().all { (from, to) ->
            segmentoDentro(from, to)
        }

    fun crearRutaInterna(origin: Posicion, destination: Posicion): RutaPeatonal {
        val nodes = pathNodes.toMutableList()
        val startIndex = nodes.size
        nodes += origin
        val destinationIndex = nodes.size
        nodes += destination
        val graph = nodes.indices.associateWith { mutableListOf<Pair<Int, Double>>() }.toMutableMap()

        pathEdges.forEach { (from, to) ->
            if (segmentoDentro(nodes[from], nodes[to])) connect(graph, from, to, nodes)
        }
        connectNearest(graph, startIndex, nodes, 5)
        connectNearest(graph, destinationIndex, nodes, 5)

        val indexes = shortestPath(graph, startIndex, destinationIndex)
        check(indexes.size > 1) { "No se encontró un trazado que permanezca dentro de TECSUP." }
        val points = indexes.map(nodes::get)
        check(rutaCompletaDentro(points)) { "El trazado interno sale del límite de TECSUP." }
        val steps = points.zipWithNext().mapIndexed { index, (from, to) ->
            val meters = distanceMeters(from, to)
            val direction = cardinalDirection(from, to)
            val instruction = if (index == 0) "Avanza por el sendero interno hacia el $direction."
            else "Continúa por el camino interno hacia el $direction."
            IndicacionRuta(instruction, meters, meters / 1.2)
        }.toMutableList()
        steps += IndicacionRuta("Has llegado a tu destino dentro del campus.", 0.0, 0.0)
        val distance = points.zipWithNext().sumOf { (from, to) -> distanceMeters(from, to) }
        return RutaPeatonal(distance, distance / 1.2, points, steps, esReferencial = true)
    }

    private fun connectNearest(
        graph: MutableMap<Int, MutableList<Pair<Int, Double>>>,
        endpoint: Int,
        nodes: List<Posicion>,
        count: Int
    ) {
        nodes.indices.filter { it != endpoint }
            .sortedBy { distanceMeters(nodes[endpoint], nodes[it]) }
            .filter { segmentoDentro(nodes[endpoint], nodes[it]) }
            .take(count)
            .forEach { connect(graph, endpoint, it, nodes) }
    }

    private fun connect(
        graph: MutableMap<Int, MutableList<Pair<Int, Double>>>,
        from: Int,
        to: Int,
        nodes: List<Posicion>
    ) {
        val distance = distanceMeters(nodes[from], nodes[to])
        graph.getValue(from).add(to to distance)
        graph.getValue(to).add(from to distance)
    }

    private fun shortestPath(
        graph: Map<Int, List<Pair<Int, Double>>>,
        start: Int,
        destination: Int
    ): List<Int> {
        val distances = graph.keys.associateWith { Double.POSITIVE_INFINITY }.toMutableMap()
        val previous = mutableMapOf<Int, Int>()
        val pending = graph.keys.toMutableSet()
        distances[start] = 0.0
        while (pending.isNotEmpty()) {
            val current = pending.minByOrNull { distances.getValue(it) } ?: break
            if (distances.getValue(current).isInfinite()) break
            pending.remove(current)
            if (current == destination) break
            graph[current].orEmpty().forEach { (next, length) ->
                val candidate = distances.getValue(current) + length
                if (candidate < distances.getValue(next)) {
                    distances[next] = candidate
                    previous[next] = current
                }
            }
        }
        if (destination != start && destination !in previous) return emptyList()
        val path = mutableListOf(destination)
        while (path.last() != start) path += previous[path.last()] ?: return emptyList()
        return path.asReversed()
    }

    private fun segmentoDentro(from: Posicion, to: Posicion): Boolean {
        val meters = distanceMeters(from, to)
        val samples = ceil(meters / 5.0).toInt().coerceAtLeast(1)
        return (0..samples).all { index ->
            val fraction = index.toDouble() / samples
            estaDentro(
                Posicion(
                    from.latitud + (to.latitud - from.latitud) * fraction,
                    from.longitud + (to.longitud - from.longitud) * fraction
                )
            )
        }
    }

    private fun distanceMeters(from: Posicion, to: Posicion): Double {
        val lat1 = Math.toRadians(from.latitud)
        val lat2 = Math.toRadians(to.latitud)
        val deltaLat = lat2 - lat1
        val deltaLon = Math.toRadians(to.longitud - from.longitud)
        val value = sin(deltaLat / 2) * sin(deltaLat / 2) +
            cos(lat1) * cos(lat2) * sin(deltaLon / 2) * sin(deltaLon / 2)
        return 2 * EARTH_RADIUS_METERS * atan2(sqrt(value), sqrt(1 - value))
    }

    private fun cardinalDirection(from: Posicion, to: Posicion): String {
        val lat1 = Math.toRadians(from.latitud)
        val lat2 = Math.toRadians(to.latitud)
        val deltaLon = Math.toRadians(to.longitud - from.longitud)
        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)
        val bearing = (Math.toDegrees(atan2(y, x)) + 360) % 360
        return listOf("norte", "noreste", "este", "sureste", "sur", "suroeste", "oeste", "noroeste")
            .getOrElse(((bearing + 22.5) / 45).toInt() % 8) { "norte" }
    }

    private fun fromReferenceImage(x: Int, y: Int) = Posicion(
        latitud = -12.0426043618 - 0.0000001529823 * x - 0.00000508367156 * y,
        longitud = -76.9545453249 + 0.00000512604781 * x + 0.0000000486253 * y
    )
}
