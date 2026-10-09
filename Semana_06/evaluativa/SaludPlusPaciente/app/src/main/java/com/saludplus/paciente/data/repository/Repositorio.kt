package com.saludplus.paciente.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.saludplus.paciente.data.model.Cita
import com.saludplus.paciente.data.model.Especialidad
import com.saludplus.paciente.data.model.EstadoCita
import com.saludplus.paciente.data.model.ErrorReserva
import com.saludplus.paciente.data.model.Medico
import com.saludplus.paciente.data.model.Usuario
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

object Repositorio {
    /** Usuarios creados durante la ejecución actual; no se persisten al cerrar la app. */
    private val usuarios = mutableStateListOf<Usuario>()
    /** Citas compartidas por las pantallas para reflejar cambios al recomponer Compose. */
    private val citas = mutableStateListOf<Cita>()

    /** Identidad con sesión activa, observada por las pantallas de perfil y agendamiento. */
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    val especialidades = listOf(
        Especialidad("cardio", "Cardiología", "Cuida la salud de tu corazón", "♥", true),
        Especialidad("pediatria", "Pediatría", "Atención para niñas y niños", "✚", true),
        Especialidad("derma", "Dermatología", "Piel, cabello y uñas", "✦", true),
        Especialidad("gineco", "Ginecología", "Salud integral de la mujer", "✿", false),
        Especialidad("trauma", "Traumatología", "Huesos, articulaciones y movimiento", "⌁", false),
        Especialidad("oftalmo", "Oftalmología", "Prevención y cuidado de la visión", "◉", false)
    )

    val medicos = listOf(
        Medico("m1", "Dra. Ana Torres", "cardio", "Cardiología", 12, 4.9, 0),
        Medico("m2", "Dra. Valeria Ríos", "pediatria", "Pediatría", 9, 4.8, 1),
        Medico("m3", "Dr. Luis Vega", "derma", "Dermatología", 15, 4.7, 2),
        Medico("m4", "Dra. Rosa Díaz", "gineco", "Ginecología", 11, 4.9, 3),
        Medico("m5", "Dra. Camila Paredes", "cardio", "Cardiología", 7, 4.6, 1),
        Medico("m6", "Dr. Marco Salazar", "trauma", "Traumatología", 14, 4.8, 2),
        Medico("m7", "Dra. Elena Campos", "oftalmo", "Oftalmología", 10, 4.7, 3)
    )

    private val horariosBase = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "14:00", "14:30", "15:00", "15:30"
    )

    init {
        usuarios.add(Usuario("demo", "Paciente Demo", "demo@saludplus.pe", "999111222", "123456"))
    }

    /** Valida también aquí para que otra pantalla no pueda saltarse las reglas del formulario. */
    fun registrarUsuario(nombre: String, correo: String, telefono: String, contrasena: String): Usuario? {
        if (Validaciones.errorRegistro(nombre, correo, telefono, contrasena) != null) return null
        val emailNormalizado = Validaciones.normalizarCorreo(correo)
        if (usuarios.any { it.correo.equals(emailNormalizado, ignoreCase = true) }) return null
        val usuario = Usuario(UUID.randomUUID().toString(), nombre.trim(), emailNormalizado, telefono.trim(), contrasena)
        usuarios.add(usuario)
        usuarioActual = usuario
        return usuario
    }

    /** Busca coincidencia de correo y contraseña, y actualiza la sesión activa. */
    fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val usuario = usuarios.firstOrNull {
            it.correo.equals(correo.trim(), ignoreCase = true) && it.contrasena == contrasena
        }
        usuarioActual = usuario
        return usuario
    }

    /** Limpia solo la sesión actual; las listas permanecen hasta cerrar la app. */
    fun cerrarSesion() {
        usuarioActual = null
    }

    /** Filtra especialidades por nombre o descripción, ignorando mayúsculas. */
    fun buscarEspecialidades(texto: String): List<Especialidad> {
        val consulta = texto.trim()
        return especialidades.filter {
            it.nombre.contains(consulta, ignoreCase = true) || it.descripcion.contains(consulta, ignoreCase = true)
        }
    }

    /** Limita la selección de Inicio a las especialidades destacadas. */
    fun especialidadesDestacadas(): List<Especialidad> = especialidades.filter { it.destacada }.take(5)

    /** Resuelve un elemento del catálogo por su identificador estable. */
    fun obtenerEspecialidad(id: String): Especialidad? = especialidades.find { it.id == id }

    /** Resuelve el profesional que se transfirió entre destinos de navegación. */
    fun obtenerMedico(id: String): Medico? = medicos.find { it.id == id }

    /** Recupera los datos de una cita para su vista de detalle. */
    fun obtenerCita(id: String): Cita? = citas.find { it.id == id }

    /** Ordena profesionales por calificación dentro de la especialidad seleccionada. */
    fun medicosPorEspecialidad(especialidadId: String): List<Medico> =
        medicos.filter { it.especialidadId == especialidadId }.sortedByDescending { it.calificacion }

    /** Aplica la búsqueda reactiva al nombre o especialidad del profesional. */
    fun buscarMedicos(especialidadId: String, texto: String): List<Medico> {
        val consulta = texto.trim()
        return medicosPorEspecialidad(especialidadId).filter {
            it.nombre.contains(consulta, ignoreCase = true) || it.especialidadNombre.contains(consulta, ignoreCase = true)
        }
    }

    /** Oculta turnos ya reservados para el mismo profesional y fecha. */
    fun horariosDisponibles(medicoId: String, fecha: LocalDate): List<String> {
        if (obtenerMedico(medicoId) == null || !Validaciones.fechaReservable(fecha)) return emptyList()
        val ocupados = citas.filter {
            it.medicoId == medicoId && it.fecha == fecha && it.estado == EstadoCita.PROGRAMADA
        }.map { it.hora }.toSet()
        return horariosBase.filterNot { it in ocupados }
    }

    /** Misma comprobación para la pantalla y para guardar: devuelve la causa concreta del rechazo. */
    fun validarReserva(especialidadId: String, medicoId: String, fecha: LocalDate?, hora: String): ErrorReserva? {
        if (usuarioActual == null) return ErrorReserva.SIN_SESION
        if (obtenerEspecialidad(especialidadId) == null) return ErrorReserva.ESPECIALIDAD_INVALIDA
        val medico = obtenerMedico(medicoId) ?: return ErrorReserva.MEDICO_INVALIDO
        if (medico.especialidadId != especialidadId) return ErrorReserva.ESPECIALIDAD_NO_COINCIDE
        if (fecha == null) return ErrorReserva.FECHA_INVALIDA
        if (fecha.isBefore(LocalDate.now())) return ErrorReserva.FECHA_PASADA
        if (fecha.dayOfWeek.value > 5) return ErrorReserva.FIN_DE_SEMANA
        if (hora !in horariosBase) return ErrorReserva.HORA_INVALIDA
        if (citas.any {
            it.medicoId == medicoId && it.fecha == fecha && it.hora == hora &&
                it.estado == EstadoCita.PROGRAMADA
        }) return ErrorReserva.HORARIO_OCUPADO
        return null
    }

    /** Comprueba el turno de nuevo antes de añadir la cita, aunque la pantalla ya lo haya validado. */
    fun agendarCita(especialidadId: String, medicoId: String, fecha: LocalDate, hora: String, motivo: String): Cita? {
        if (validarReserva(especialidadId, medicoId, fecha, hora) != null) return null
        val usuarioId = usuarioActual?.id ?: return null
        val cita = Cita(UUID.randomUUID().toString(), usuarioId, medicoId, especialidadId, fecha, hora, motivo.trim())
        citas.add(cita)
        return cita
    }

    /** Lista las citas del paciente actual en orden cronológico. */
    fun citasDelUsuario(usuarioId: String = usuarioActual?.id.orEmpty()): List<Cita> =
        citas.filter { it.usuarioId == usuarioId }.sortedWith(compareBy<Cita> { it.fecha }.thenBy { it.hora })

    /** Devuelve la cita futura más cercana del paciente actual, descartando horas ya transcurridas. */
    fun proximaCitaDelUsuario(ahora: LocalDateTime = LocalDateTime.now()): Cita? =
        citasDelUsuario().firstOrNull {
            it.estado == EstadoCita.PROGRAMADA &&
                !LocalDateTime.of(it.fecha, LocalTime.parse(it.hora)).isBefore(ahora)
        }

    /** Quita la cita local y devuelve si encontró algún elemento para cancelar. */
    fun cancelarCita(citaId: String): Boolean = citas.removeAll { it.id == citaId }

    /** Devuelve los cinco días hábiles de la semana solicitada, sin ofrecer fechas pasadas. */
    fun cincoDiasHabiles(inicioSemana: LocalDate): List<LocalDate> {
        val hoy = LocalDate.now()
        var fecha = if (inicioSemana.isBefore(hoy)) hoy else inicioSemana
        if (fecha.dayOfWeek.value > 5) fecha = fecha.plusDays((8 - fecha.dayOfWeek.value).toLong())
        val resultado = mutableListOf<LocalDate>()
        while (resultado.size < 5) {
            if (fecha.dayOfWeek.value <= 5 && !fecha.isBefore(hoy)) resultado += fecha
            fecha = fecha.plusDays(1)
        }
        return resultado
    }

    internal fun restablecerParaPruebas() {
        usuarios.clear()
        usuarios.add(Usuario("demo", "Paciente Demo", "demo@saludplus.pe", "999111222", "123456"))
        citas.clear()
        usuarioActual = null
    }
}

