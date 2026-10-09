package com.saludplus.paciente.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue
import java.time.DayOfWeek
import java.time.LocalDate

class RepositorioTest {
    @Before
    fun prepararDatos() {
        Repositorio.restablecerParaPruebas()
        Repositorio.iniciarSesion("demo@saludplus.pe", "123456")
    }

    @Test
    fun `registro no permite duplicar correos`() {
        val primerRegistro = Repositorio.registrarUsuario("Paciente Nuevo", "nuevo@correo.pe", "999000111", "clave123")
        val duplicado = Repositorio.registrarUsuario("Otra Persona", "NUEVO@correo.pe", "999000222", "clave456")

        assertNotNull(primerRegistro)
        assertNull(duplicado)
    }

    @Test
    fun `un horario reservado deja de estar disponible`() {
        val fecha = LocalDate.of(2026, 10, 19)
        val reserva = Repositorio.agendarCita("cardio", "m1", fecha, "08:00", "Control")

        assertNotNull(reserva)
        assertEquals(false, "08:00" in Repositorio.horariosDisponibles("m1", fecha))
    }

    @Test
    fun `no se puede reservar dos veces el mismo turno del medico`() {
        val fecha = LocalDate.of(2026, 10, 20)
        val primera = Repositorio.agendarCita("cardio", "m1", fecha, "09:00", "Control")
        val segunda = Repositorio.agendarCita("cardio", "m1", fecha, "09:00", "Seguimiento")

        assertNotNull(primera)
        assertNull(segunda)
    }

    @Test
    fun `el calendario muestra cinco dias habiles sin fechas pasadas`() {
        val dias = Repositorio.cincoDiasHabiles(LocalDate.now())

        assertEquals(5, dias.size)
        assertTrue(dias.all { it.dayOfWeek.value in 1..5 })
        assertTrue(dias.all { !it.isBefore(LocalDate.now()) })
        assertTrue(dias.zipWithNext().all { (primero, segundo) -> primero.isBefore(segundo) })
    }

    @Test
    fun `una semana que empieza en fin de semana avanza a dias laborables`() {
        val sabado = LocalDate.now().with(DayOfWeek.SATURDAY)
        val dias = Repositorio.cincoDiasHabiles(sabado)

        assertEquals(5, dias.size)
        assertTrue(dias.all { it.dayOfWeek.value in 1..5 })
    }
}

