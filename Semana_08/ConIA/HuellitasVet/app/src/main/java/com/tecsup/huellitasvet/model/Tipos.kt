package com.tecsup.huellitasvet.model

import java.time.LocalDate
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class Rol { CLIENTE, VETERINARIO, ADMIN }
enum class EstadoCita { PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA }
enum class Especie { PERRO, GATO, OTRO }
enum class Sexo { MACHO, HEMBRA }
data class ConteoServicio(val nombre: String, val cantidad: Int)
data class UltimaVisita(val mascotaId: Int, val fecha: String?)
data class DatoGrafico(val etiqueta: String, val valor: Float)

object Horarios {
    private val formato = DateTimeFormatter.ofPattern("HH:mm")
    fun paraFecha(fecha: LocalDate, duracionMin: Int = 30): List<String> {
        val apertura = LocalTime.of(10, 0)
        val cierre = if (fecha.dayOfWeek == DayOfWeek.SUNDAY) LocalTime.of(13, 0) else LocalTime.of(18, 0)
        return generateSequence(apertura) { it.plusMinutes(30) }
            .takeWhile { it.isBefore(cierre) }
            .filter { it.plusMinutes(duracionMin.toLong()) <= cierre }
            .map { it.format(formato) }
            .toList()
    }
}
