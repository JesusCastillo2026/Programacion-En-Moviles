package com.tecsup.campusgps.data

import android.net.Uri
import com.tecsup.campusgps.model.IndicacionRuta
import com.tecsup.campusgps.model.Posicion
import com.tecsup.campusgps.model.RutaPeatonal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

interface RutasRepository {
    suspend fun obtenerRuta(origen: Posicion, destino: Posicion): RutaPeatonal
}

class ValhallaRutasRepository : RutasRepository {
    override suspend fun obtenerRuta(origen: Posicion, destino: Posicion): RutaPeatonal =
        withContext(Dispatchers.IO) {
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
            try {
                connection.connectTimeout = 12_000
                connection.readTimeout = 15_000
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode
                val body = (if (responseCode in 200..299) connection.inputStream else connection.errorStream)
                    ?.bufferedReader()?.use { it.readText() }.orEmpty()
                check(responseCode in 200..299) { "El servicio de rutas respondió $responseCode." }
                val trip = JSONObject(body).getJSONObject("trip")
                val summary = trip.getJSONObject("summary")
                val leg = trip.getJSONArray("legs").getJSONObject(0)
                val puntos = decodificarPolyline6(leg.getString("shape"))
                val maniobras = leg.getJSONArray("maneuvers")
                val indicaciones = (0 until maniobras.length()).mapNotNull { index ->
                    val maneuver = maniobras.getJSONObject(index)
                    maneuver.optString("instruction")
                        .takeIf(String::isNotBlank)
                        ?.let { IndicacionRuta(it, maneuver.optDouble("length") * 1000.0, maneuver.optDouble("time")) }
                }
                check(puntos.size > 1 && indicaciones.isNotEmpty()) { "No se encontró un recorrido peatonal para esos puntos." }
                RutaPeatonal(
                    distanciaMetros = summary.getDouble("length") * 1000.0,
                    duracionSegundos = summary.getDouble("time"),
                    puntos = puntos,
                    indicaciones = indicaciones
                )
            } finally {
                connection.disconnect()
            }
        }

    private fun decodificarPolyline6(encoded: String): List<Posicion> {
        val result = mutableListOf<Posicion>()
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
            result += Posicion(latitude / 1_000_000.0, longitude / 1_000_000.0)
        }
        return result
    }
}
