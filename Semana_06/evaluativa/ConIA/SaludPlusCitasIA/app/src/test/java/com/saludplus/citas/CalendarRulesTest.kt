package com.saludplus.citas

import com.saludplus.citas.data.fechaEnEspanol
import com.saludplus.citas.data.proximosDiasHabiles
import com.saludplus.citas.data.tituloPeriodo
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

/** Pruebas de calendario con fechas fijas para cubrir fines de semana y cambios de mes. */
class CalendarRulesTest {
    @Test
    fun domingoMuestraCincoDiasDeLaSemanaSiguiente() {
        val hoy = LocalDate.of(2026, 10, 4)
        val dias = proximosDiasHabiles(hoy, 0)
        assertEquals(LocalDate.of(2026, 10, 5), dias.first())
        assertEquals(LocalDate.of(2026, 10, 9), dias.last())
        assertEquals(5, dias.size)
        assertTrue(dias.all { it.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) })
    }

    @Test
    fun semanaSiguienteAvanzaSinMostrarPasado() {
        val hoy = LocalDate.of(2026, 10, 7)
        val actual = proximosDiasHabiles(hoy, 0)
        val siguiente = proximosDiasHabiles(hoy, 1)
        assertEquals(LocalDate.of(2026, 10, 7), actual.first())
        assertEquals(LocalDate.of(2026, 10, 14), siguiente.first())
        assertTrue(actual.all { !it.isBefore(hoy) })
        assertTrue(siguiente.all { !it.isBefore(hoy) })
    }

    @Test
    fun tituloYCitaReflejanMesYTextoEnEspanol() {
        val dias = proximosDiasHabiles(LocalDate.of(2026, 10, 28), 0)
        assertEquals("Octubre 2026 · Noviembre 2026", tituloPeriodo(dias))
        assertEquals("Martes 15 de setiembre 2026",
            fechaEnEspanol(LocalDate.of(2026, 9, 15)))
    }
}
