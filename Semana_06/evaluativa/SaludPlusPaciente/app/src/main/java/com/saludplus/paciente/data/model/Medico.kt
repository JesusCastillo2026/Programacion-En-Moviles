package com.saludplus.paciente.data.model

import java.time.DayOfWeek

data class Medico(
    val id: String,
    val nombre: String,
    val especialidadId: String,
    val especialidadNombre: String,
    val experiencia: Int,
    val calificacion: Double,
    val indiceImagen: Int,
    val sedesIds: Set<String>,
    val diasAtencion: Set<DayOfWeek> = emptySet()
)

