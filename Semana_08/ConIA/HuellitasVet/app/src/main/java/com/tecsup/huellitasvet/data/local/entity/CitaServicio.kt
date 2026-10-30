package com.tecsup.huellitasvet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cita_servicios",
    primaryKeys = ["citaId", "servicioId"],
    foreignKeys = [
        ForeignKey(entity = Cita::class, parentColumns = ["id"], childColumns = ["citaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Servicio::class, parentColumns = ["id"], childColumns = ["servicioId"])
    ],
    indices = [Index("servicioId")]
)
data class CitaServicio(val citaId: Int, val servicioId: Int)
