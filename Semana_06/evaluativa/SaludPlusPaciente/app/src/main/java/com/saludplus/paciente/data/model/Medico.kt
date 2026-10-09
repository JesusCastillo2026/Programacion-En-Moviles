package com.saludplus.paciente.data.model

data class Medico(
    val id: String,
    val nombre: String,
    val especialidadId: String,
    val especialidadNombre: String,
    val experiencia: Int,
    val calificacion: Double,
    val indiceImagen: Int
)

