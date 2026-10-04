package com.saludplus.citas.data.model

data class Medico(
    val id: Int,
    val especialidadId: Int,
    val nombre: String,
    val experiencia: String,
    val calificacion: Double
)
