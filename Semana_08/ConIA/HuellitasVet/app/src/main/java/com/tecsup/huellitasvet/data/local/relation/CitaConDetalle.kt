package com.tecsup.huellitasvet.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.CitaServicio
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio

data class CitaConDetalle(
    @Embedded val cita: Cita,
    @Relation(entity = Mascota::class, parentColumn = "mascotaId", entityColumn = "id")
    val mascota: MascotaConDueno,
    @Relation(parentColumn = "servicioId", entityColumn = "id") val servicio: Servicio,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(value = CitaServicio::class, parentColumn = "citaId", entityColumn = "servicioId")
    ) val servicios: List<Servicio> = emptyList()
) {
    val nombresServicios: String
        get() = (servicios.ifEmpty { listOf(servicio) }).joinToString(" · ") { it.nombre }
}
