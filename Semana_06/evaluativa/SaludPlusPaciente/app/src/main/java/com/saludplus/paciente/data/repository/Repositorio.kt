package com.saludplus.paciente.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.saludplus.paciente.data.model.Cita
import com.saludplus.paciente.data.model.Especialidad
import com.saludplus.paciente.data.model.EstadoCita
import com.saludplus.paciente.data.model.Medico
import com.saludplus.paciente.data.model.Usuario
import java.time.LocalDate
import java.util.UUID

object Repositorio {
    private val usuarios = mutableStateListOf<Usuario>()
    private val citas = mutableStateListOf<Cita>()

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
        usuarios.add(Usuario("demo", "Paciente Demo", "demo@saludplus.pe", "999 111 222", "123456"))
    }

    fun registrarUsuario(nombre: String, correo: String, telefono: String, contrasena: String): Usuario? {
        val emailNormalizado = correo.trim().lowercase()
        if (usuarios.any { it.correo.equals(emailNormalizado, ignoreCase = true) }) return null
        val usuario = Usuario(UUID.randomUUID().toString(), nombre.trim(), emailNormalizado, telefono.trim(), contrasena)
        usuarios.add(usuario)
        usuarioActual = usuario
        return usuario
    }

    fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val usuario = usuarios.firstOrNull {
            it.correo.equals(correo.trim(), ignoreCase = true) && it.contrasena == contrasena
        }
        usuarioActual = usuario
        return usuario
    }

    fun cerrarSesion() {
        usuarioActual = null
    }

    fun buscarEspecialidades(texto: String): List<Especialidad> {
        val consulta = texto.trim()
        return especialidades.filter {
            it.nombre.contains(consulta, ignoreCase = true) || it.descripcion.contains(consulta, ignoreCase = true)
        }
    }

    fun especialidadesDestacadas(): List<Especialidad> = especialidades.filter { it.destacada }.take(5)

    fun obtenerEspecialidad(id: String): Especialidad? = especialidades.find { it.id == id }

    fun obtenerMedico(id: String): Medico? = medicos.find { it.id == id }

    fun obtenerCita(id: String): Cita? = citas.find { it.id == id }

    fun medicosPorEspecialidad(especialidadId: String): List<Medico> =
        medicos.filter { it.especialidadId == especialidadId }.sortedByDescending { it.calificacion }

    fun buscarMedicos(especialidadId: String, texto: String): List<Medico> {
        val consulta = texto.trim()
        return medicosPorEspecialidad(especialidadId).filter {
            it.nombre.contains(consulta, ignoreCase = true) || it.especialidadNombre.contains(consulta, ignoreCase = true)
        }
    }

    fun horariosDisponibles(medicoId: String, fecha: LocalDate): List<String> {
        val ocupados = citas.filter {
            it.medicoId == medicoId && it.fecha == fecha && it.estado == EstadoCita.PROGRAMADA
        }.map { it.hora }.toSet()
        return horariosBase.filterNot { it in ocupados }
    }

    fun agendarCita(especialidadId: String, medicoId: String, fecha: LocalDate, hora: String, motivo: String): Cita? {
        val usuarioId = usuarioActual?.id ?: return null
        if (obtenerEspecialidad(especialidadId) == null || obtenerMedico(medicoId) == null) return null
        if (hora !in horariosDisponibles(medicoId, fecha)) return null
        val cita = Cita(UUID.randomUUID().toString(), usuarioId, medicoId, especialidadId, fecha, hora, motivo.trim())
        citas.add(cita)
        return cita
    }

    fun citasDelUsuario(usuarioId: String = usuarioActual?.id.orEmpty()): List<Cita> =
        citas.filter { it.usuarioId == usuarioId }.sortedWith(compareBy<Cita> { it.fecha }.thenBy { it.hora })

    fun cancelarCita(citaId: String): Boolean = citas.removeAll { it.id == citaId }

    internal fun restablecerParaPruebas() {
        usuarios.clear()
        usuarios.add(Usuario("demo", "Paciente Demo", "demo@saludplus.pe", "999 111 222", "123456"))
        citas.clear()
        usuarioActual = null
    }
}

