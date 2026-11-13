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
        val fecha = LocalDate.now().plusWeeks(1).with(DayOfWeek.MONDAY)
        val reserva = Repositorio.agendarCita("sjl", "cardio", "m1", fecha, "08:00", "Control")

        assertNotNull(reserva)
        assertEquals(false, "08:00" in Repositorio.horariosDisponibles("sjl", "m1", fecha))
    }

    @Test
    fun `no se puede reservar dos veces el mismo turno del medico`() {
        val fecha = LocalDate.now().plusWeeks(1).with(DayOfWeek.TUESDAY)
        val primera = Repositorio.agendarCita("sjl", "cardio", "m1", fecha, "09:00", "Control")
        val segunda = Repositorio.agendarCita("molina", "cardio", "m1", fecha, "09:00", "Seguimiento")

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

    @Test
    fun `el repositorio rechaza datos de registro invalidos sin cambiar la sesion`() {
        listOf("@dominio.pe", "a@.", "a..b@dominio.pe", "a@-dominio.pe", "a b@dominio.pe").forEach {
            assertNull(Repositorio.registrarUsuario("Paciente", it, "999000111", "clave123"))
        }
        listOf("899000111", "99900011", "9990001110", "999abc111").forEach {
            assertNull(Repositorio.registrarUsuario("Paciente", "persona@gmail.com", it, "clave123"))
        }
        assertEquals("demo", Repositorio.usuarioActual?.id)
    }

    @Test
    fun `acepta correo institucional y normaliza espacios y mayusculas`() {
        val usuario = Repositorio.registrarUsuario(" Jesus ", " JESUS@TECSUP.EDU.PE ", "999000111", "clave123")
        assertNotNull(usuario)
        assertEquals("jesus@tecsup.edu.pe", usuario?.correo)
        assertNotNull(Repositorio.iniciarSesion("JESUS@TECSUP.EDU.PE", "clave123"))
    }

    @Test
    fun `no crea citas para otra especialidad ni fechas u horas invalidas`() {
        val fecha = LocalDate.now().plusWeeks(1).with(DayOfWeek.MONDAY)
        assertNull(Repositorio.agendarCita("sjl", "pediatria", "m1", fecha, "09:00", ""))
        assertNull(Repositorio.agendarCita("sjl", "cardio", "desconocido", fecha, "09:00", ""))
        assertNull(Repositorio.agendarCita("sjl", "cardio", "m1", LocalDate.now().minusDays(1), "09:00", ""))
        assertNull(Repositorio.agendarCita("sjl", "cardio", "m1", fecha.with(DayOfWeek.SATURDAY), "09:00", ""))
        assertNull(Repositorio.agendarCita("sjl", "cardio", "m1", fecha, "25:00", ""))
        assertTrue(Repositorio.citasDelUsuario().isEmpty())
        assertTrue(Repositorio.horariosDisponibles("sjl", "m1", fecha.with(DayOfWeek.SUNDAY)).isEmpty())
    }

    @Test
    fun `cancelar libera el turno y cada medico tiene disponibilidad independiente`() {
        val fecha = LocalDate.now().plusWeeks(1).with(DayOfWeek.MONDAY)
        val cita = Repositorio.agendarCita("sjl", "cardio", "m1", fecha, "09:00", "")
        assertNotNull(cita)
        assertNotNull(Repositorio.agendarCita("santa-anita", "cardio", "m5", fecha, "09:00", ""))
        assertTrue(Repositorio.cancelarCita(cita!!.id))
        assertTrue("09:00" in Repositorio.horariosDisponibles("sjl", "m1", fecha))
        assertNotNull(Repositorio.agendarCita("sjl", "cardio", "m1", fecha, "09:00", ""))
        Repositorio.cerrarSesion()
        assertNull(Repositorio.agendarCita("sjl", "cardio", "m1", fecha, "10:00", ""))
    }

    @Test
    fun `crear cuenta exige iniciar sesion de manera explicita`() {
        Repositorio.cerrarSesion()
        val creada = Repositorio.registrarUsuario("Paciente Nueva", "nueva@tecsup.edu.pe", "999000111", "clave123")
        assertNotNull(creada)
        assertNull(Repositorio.usuarioActual)
        assertNotNull(Repositorio.iniciarSesion("nueva@tecsup.edu.pe", "clave123"))
    }

    @Test
    fun `la sede limita especialidades medicos y reservas`() {
        val fecha = LocalDate.now().plusWeeks(1).with(DayOfWeek.MONDAY)
        assertTrue(Repositorio.especialidadesPorSede("sjl").none { it.id == "trauma" })
        assertTrue(Repositorio.medicosPorEspecialidad("cardio", "sjl").none { it.id == "m5" })
        assertNull(Repositorio.agendarCita("", "cardio", "m1", fecha, "09:00", ""))
        assertNull(Repositorio.agendarCita("sjl", "cardio", "m5", fecha, "09:00", ""))
        val cita = Repositorio.agendarCita("molina", "cardio", "m1", fecha, "09:00", "")
        assertEquals("molina", cita?.sedeId)
    }
}

