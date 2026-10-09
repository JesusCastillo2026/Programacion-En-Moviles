package com.saludplus.paciente.data.repository

import androidx.compose.runtime.saveable.SaverScope
import com.saludplus.paciente.data.model.BorradorReserva
import com.saludplus.paciente.data.model.ErrorReserva
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class ExperienciaReservaTest {
    private val dia = LocalDate.now().plusWeeks(1).with(DayOfWeek.MONDAY)

    @Before fun iniciar() {
        Repositorio.restablecerParaPruebas()
        Repositorio.iniciarSesion("demo@saludplus.pe", "123456")
    }

    private fun borradorCompleto() = BorradorReserva().apply {
        elegirEspecialidad("cardio")
        elegirMedico("m1")
        elegirFecha(dia.toString())
        hora = "09:00"
        motivo = "Control"
    }

    @Test fun `volver y elegir el mismo medico conserva dia hora y motivo`() {
        val borrador = borradorCompleto()
        borrador.elegirEspecialidad("cardio")
        borrador.elegirMedico("m1")
        assertEquals(dia.toString(), borrador.fecha)
        assertEquals("09:00", borrador.hora)
        assertEquals("Control", borrador.motivo)
    }

    @Test fun `cambiar medico o especialidad invalida las selecciones dependientes`() {
        val borrador = borradorCompleto()
        borrador.elegirMedico("m5")
        assertEquals("cardio", borrador.especialidadId)
        assertEquals("", borrador.fecha)
        assertEquals("", borrador.hora)
        borrador.elegirEspecialidad("pediatria")
        assertEquals("", borrador.medicoId)
        assertEquals("", borrador.motivo)
    }

    @Test fun `cambiar dia quita la hora y cambiar semana quita el dia`() {
        val borrador = borradorCompleto()
        borrador.elegirFecha(dia.plusDays(1).toString())
        assertEquals("", borrador.hora)
        borrador.cambiarSemana(dia.plusWeeks(1).toString())
        assertEquals("", borrador.fecha)
        assertEquals("m1", borrador.medicoId)
    }

    @Test fun `terminar o comenzar otra reserva limpia el borrador`() {
        val borrador = borradorCompleto()
        borrador.limpiar()
        assertEquals(listOf("", "", "", "", ""),
            listOf(borrador.especialidadId, borrador.medicoId, borrador.fecha, borrador.hora, borrador.motivo))
    }

    @Test fun `estado guardable restaura todos los datos del flujo`() {
        val original = borradorCompleto()
        val scope = object : SaverScope { override fun canBeSaved(value: Any) = true }
        val datos = with(BorradorReserva.saver) { scope.save(original) }!!
        val copia = BorradorReserva.saver.restore(datos)!!
        assertEquals(original.especialidadId, copia.especialidadId)
        assertEquals(original.medicoId, copia.medicoId)
        assertEquals(original.fecha, copia.fecha)
        assertEquals(original.hora, copia.hora)
        assertEquals(original.semana, copia.semana)
        assertEquals(original.motivo, copia.motivo)
    }

    @Test fun `proxima cita respeta orden horario usuario y cancelacion`() {
        val tarde = Repositorio.agendarCita("cardio", "m1", dia, "10:00", "")!!
        val temprano = Repositorio.agendarCita("cardio", "m1", dia, "08:00", "")!!
        assertEquals(temprano.id, Repositorio.proximaCitaDelUsuario(dia.atStartOfDay())?.id)
        assertEquals(tarde.id, Repositorio.proximaCitaDelUsuario(dia.atTime(8, 1))?.id)
        assertNull(Repositorio.proximaCitaDelUsuario(dia.atTime(16, 0)))
        Repositorio.cancelarCita(temprano.id)
        assertEquals(tarde.id, Repositorio.proximaCitaDelUsuario(dia.atStartOfDay())?.id)
        Repositorio.registrarUsuario("Otro paciente", "otro@tecsup.edu.pe", "999000111", "clave123")
        assertNull(Repositorio.proximaCitaDelUsuario(dia.atStartOfDay()))
        Repositorio.cerrarSesion()
        assertNull(Repositorio.proximaCitaDelUsuario(dia.atStartOfDay()))
    }

    @Test fun `cada problema de reserva produce una causa identificable`() {
        assertNull(Repositorio.validarReserva("cardio", "m1", dia, "09:00"))
        assertEquals(ErrorReserva.ESPECIALIDAD_INVALIDA, Repositorio.validarReserva("otra", "m1", dia, "09:00"))
        assertEquals(ErrorReserva.MEDICO_INVALIDO, Repositorio.validarReserva("cardio", "otro", dia, "09:00"))
        assertEquals(ErrorReserva.ESPECIALIDAD_NO_COINCIDE, Repositorio.validarReserva("pediatria", "m1", dia, "09:00"))
        assertEquals(ErrorReserva.FECHA_INVALIDA, Repositorio.validarReserva("cardio", "m1", null, "09:00"))
        assertEquals(ErrorReserva.FECHA_PASADA, Repositorio.validarReserva("cardio", "m1", LocalDate.now().minusDays(1), "09:00"))
        assertEquals(ErrorReserva.FIN_DE_SEMANA, Repositorio.validarReserva("cardio", "m1", dia.with(DayOfWeek.SATURDAY), "09:00"))
        assertEquals(ErrorReserva.HORA_INVALIDA, Repositorio.validarReserva("cardio", "m1", dia, "25:00"))
        Repositorio.agendarCita("cardio", "m1", dia, "09:00", "")
        assertEquals(ErrorReserva.HORARIO_OCUPADO, Repositorio.validarReserva("cardio", "m1", dia, "09:00"))
        Repositorio.cerrarSesion()
        assertEquals(ErrorReserva.SIN_SESION, Repositorio.validarReserva("cardio", "m1", dia, "09:00"))
    }
}
