package com.tecsup.huellitasvet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "citas",
    foreignKeys = [
        ForeignKey(entity = Mascota::class, parentColumns = ["id"], childColumns = ["mascotaId"]),
        ForeignKey(entity = Servicio::class, parentColumns = ["id"], childColumns = ["servicioId"])
    ],
    indices = [Index("mascotaId"), Index("servicioId"), Index(value = ["fecha", "hora"])]
)
data class Cita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mascotaId: Int,
    val servicioId: Int,
    val fecha: String,
    val hora: String,
    val nota: String,
    val estado: String,
    val precio: Double,
    val diagnostico: String? = null,
    val tratamiento: String? = null,
    val pesoRegistrado: Double? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)
