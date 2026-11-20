package com.tecsup.campusgps.model

const val TECSUP_LIMA_LATITUD = -12.04398
const val TECSUP_LIMA_LONGITUD = -76.95291

data class Posicion(val latitud: Double, val longitud: Double)

data class IndicacionRuta(
    val instruccion: String,
    val distanciaMetros: Double,
    val duracionSegundos: Double
)

data class RutaPeatonal(
    val distanciaMetros: Double,
    val duracionSegundos: Double,
    val puntos: List<Posicion>,
    val indicaciones: List<IndicacionRuta>
)

data class Lugar(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double
)

data class LugarConDistancia(
    val lugar: Lugar,
    val metros: Float?,
    val dentroDelRadio: Boolean? = null
)

sealed interface UbicacionUi {
    data object Inicial : UbicacionUi
    data object Buscando : UbicacionUi
    data class Lista(val posicion: Posicion) : UbicacionUi
    data class Error(val mensaje: String, val tipo: TipoError) : UbicacionUi
}

enum class TipoError { SIN_PERMISO, GPS_APAGADO, SIN_PLAY_SERVICES, OTRO }
