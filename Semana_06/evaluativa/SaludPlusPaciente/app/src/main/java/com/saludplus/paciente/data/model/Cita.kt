package com.saludplus.paciente.data.model

import java.time.LocalDate

data class Cita(
    val id: String,
    val usuarioId: String,
    val medicoId: String,
    val especialidadId: String,
    val fecha: LocalDate,
    val hora: String,
    val motivo: String,
    val estado: EstadoCita = EstadoCita.PROGRAMADA
)

enum class EstadoCita {
    PROGRAMADA,
    COMPLETADA
}

