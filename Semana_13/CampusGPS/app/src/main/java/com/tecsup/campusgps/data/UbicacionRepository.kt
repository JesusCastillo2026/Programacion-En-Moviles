package com.tecsup.campusgps.data

import com.tecsup.campusgps.model.Posicion

interface UbicacionRepository {
    fun playServicesDisponible(): Boolean
    fun ubicacionActiva(): Boolean
    suspend fun obtenerPosicion(): Result<Posicion>
}
