package com.saludplus.citas

import com.saludplus.citas.data.repository.Repositorio
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RepositorioTest {
    @Before
    fun preparar() {
        Repositorio.cerrarSesion()
        Repositorio.usuarios.clear()
        Repositorio.citas.clear()
    }

    @After
    fun limpiar() = preparar()

    @Test
    fun registroYLoginValidanCredenciales() {
        assertNull(Repositorio.registrarUsuario("", "a@b.com", "123456"))
        assertNotNull(Repositorio.registrarUsuario("Jesús Castillo", "jesus@tecsup.edu.pe", "123456"))
        assertNull(Repositorio.registrarUsuario("Otro", "jesus@tecsup.edu.pe", "123456"))
        Repositorio.cerrarSesion()
        assertNull(Repositorio.iniciarSesion("jesus@tecsup.edu.pe", "incorrecta"))
        assertNotNull(Repositorio.iniciarSesion("jesus@tecsup.edu.pe", "123456"))
    }

    @Test
    fun citaReservadaBloqueaHorarioYSeLiberaAlCancelar() {
        val usuario = Repositorio.registrarUsuario("Jesús", "jesus@tecsup.edu.pe", "123456")
        assertNotNull(usuario)
        val cita = Repositorio.agendarCita(1, "2026-10-12", "09:00")
        assertNotNull(cita)
        assertFalse("09:00" in Repositorio.horariosDisponibles(1, "2026-10-12"))
        assertNull(Repositorio.agendarCita(1, "2026-10-12", "09:00"))
        assertTrue(Repositorio.cancelarCita(cita!!.id))
        assertTrue("09:00" in Repositorio.horariosDisponibles(1, "2026-10-12"))
    }

    @Test
    fun busquedaYOrdenDeMedicosFuncionan() {
        assertEquals("Cardiología", Repositorio.buscarEspecialidades("cardio").single().nombre)
        val medicos = Repositorio.medicosPorEspecialidad(1)
        assertTrue(medicos.zipWithNext().all { it.first.calificacion >= it.second.calificacion })
    }
}
