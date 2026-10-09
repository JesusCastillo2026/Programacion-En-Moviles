package com.saludplus.paciente.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Selecciones del flujo actual; se conservan al retroceder y se limpian al completar o iniciar otra reserva. */
class BorradorReserva {
    var especialidadId by mutableStateOf("")
    var medicoId by mutableStateOf("")
    var fecha by mutableStateOf("")
    var hora by mutableStateOf("")
    var semana by mutableStateOf(semanaActual())
    var motivo by mutableStateOf("")

    fun elegirEspecialidad(id: String) {
        if (especialidadId != id) {
            limpiar()
            especialidadId = id
        }
    }

    fun elegirMedico(id: String) {
        if (medicoId != id) {
            medicoId = id
            fecha = ""
            hora = ""
            semana = semanaActual()
        }
    }

    fun elegirFecha(valor: String) {
        if (fecha != valor) hora = ""
        fecha = valor
    }

    fun cambiarSemana(valor: String) {
        semana = valor
        fecha = ""
        hora = ""
    }

    fun limpiar() {
        especialidadId = ""
        medicoId = ""
        fecha = ""
        hora = ""
        semana = semanaActual()
        motivo = ""
    }

    companion object {
        private fun semanaActual(): String = RelojClinica.hoy()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()

        /** Solo se guardan identificadores y fechas ISO; no se duplica el repositorio de citas. */
        val saver = Saver<BorradorReserva, List<String>>(
            save = { listOf(it.especialidadId, it.medicoId, it.fecha, it.hora, it.semana, it.motivo) },
            restore = { valores -> BorradorReserva().apply {
                especialidadId = valores[0]
                medicoId = valores[1]
                fecha = valores[2]
                hora = valores[3]
                semana = valores[4]
                motivo = valores.getOrElse(5) { "" }
            } }
        )
    }
}
