package com.tecsup.huellitasvet.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class Rol { CLIENTE, ADMIN }
enum class EstadoCita { PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA }
enum class Especie { PERRO, GATO, OTRO }
enum class Sexo { MACHO, HEMBRA }
data class ConteoServicio(val nombre: String, val cantidad: Int)
data class UltimaVisita(val mascotaId: Int, val fecha: String?)
data class DatoGrafico(val etiqueta: String, val valor: Float)

object Horarios {
    private val formato = DateTimeFormatter.ofPattern("HH:mm")
    val todos: List<String> = (0 until 18).map { LocalTime.of(9, 0).plusMinutes(it * 30L).format(formato) }
}
