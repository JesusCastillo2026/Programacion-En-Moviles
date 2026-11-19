package com.tecsup.campusgps.data

import com.tecsup.campusgps.model.Posicion
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {
    fun playServicesDisponible(): Boolean
    fun ubicacionActiva(): Boolean
    suspend fun obtenerPosicion(): Result<Posicion>
    fun seguirUbicacion(): Flow<Posicion>
}
