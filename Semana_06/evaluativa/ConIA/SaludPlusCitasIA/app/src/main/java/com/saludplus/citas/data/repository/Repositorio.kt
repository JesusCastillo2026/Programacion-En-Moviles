package com.saludplus.citas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.model.Usuario

/** Fuente única de datos de la app: comparte listas reactivas entre todas las pantallas. */
object Repositorio {
    // Las listas viven solo en memoria; Compose observa los cambios de usuarios y citas.
    val usuarios = mutableStateListOf<Usuario>()
    val especialidades = mutableStateListOf(
        Especialidad(1, "Cardiología", "Cuidado de tu corazón", "♥"),
        Especialidad(2, "Pediatría", "Salud para los más pequeños", "☀"),
        Especialidad(3, "Dermatología", "Cuidado de la piel", "✿"),
        Especialidad(4, "Medicina general", "Atención integral", "+"),
        Especialidad(5, "Neurología", "Sistema nervioso", "●")
    )
    val medicos = mutableStateListOf(
        Medico(1, 1, "Dra. Ana Torres", "12 años de experiencia", 4.9),
        Medico(2, 1, "Dr. Carlos Ríos", "9 años de experiencia", 4.8),
        Medico(3, 2, "Dr. Luis Vega", "10 años de experiencia", 4.7),
        Medico(4, 2, "Dra. Elena Paredes", "8 años de experiencia", 4.8),
        Medico(5, 3, "Dra. Rosa Díaz", "8 años de experiencia", 4.8),
        Medico(6, 4, "Dr. Miguel Salazar", "11 años de experiencia", 4.9),
        Medico(7, 5, "Dra. Paula Méndez", "7 años de experiencia", 4.7)
    )
    val citas = mutableStateListOf<Cita>()
    val horariosBase = listOf("08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00")
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    /** Valida el formulario, evita correos repetidos e inicia la sesión al registrar. */
    fun registrarUsuario(nombre: String, correo: String, clave: String): Usuario? {
        val correoLimpio = correo.trim().lowercase()
        if (nombre.isBlank() || !correoLimpio.contains("@") || clave.length < 6 ||
            usuarios.any { it.correo == correoLimpio }) return null
        val usuario = Usuario((usuarios.maxOfOrNull { it.id } ?: 0) + 1, nombre.trim(), correoLimpio, clave)
        usuarios.add(usuario)
        usuarioActual = usuario
        return usuario
    }

    /** Localiza al usuario con credenciales coincidentes y actualiza la sesión compartida. */
    fun iniciarSesion(correo: String, clave: String): Usuario? {
        val usuario = usuarios.find { it.correo == correo.trim().lowercase() && it.clave == clave }
        usuarioActual = usuario
        return usuario
    }

    /** Cierra la sesión sin borrar los datos temporales de esta ejecución. */
    fun cerrarSesion() { usuarioActual = null }

    /** La búsqueda filtra al escribir, sin mantener una segunda lista. */
    fun buscarEspecialidades(texto: String): List<Especialidad> =
        especialidades.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    /** Selección breve para el carrusel de Inicio. */
    fun especialidadesDestacadas(): List<Especialidad> = especialidades.take(4)
    /** Resolución de identificadores recibidos por navegación. */
    fun obtenerEspecialidad(id: Int): Especialidad? = especialidades.find { it.id == id }
    fun obtenerMedico(id: Int): Medico? = medicos.find { it.id == id }
    fun obtenerCita(id: Int): Cita? = citas.find { it.id == id }

    /** Lista los profesionales de la especialidad con mejor valoración primero. */
    fun medicosPorEspecialidad(especialidadId: Int): List<Medico> =
        medicos.filter { it.especialidadId == especialidadId }.sortedByDescending { it.calificacion }

    /** Búsqueda global de médicos, útil para ampliar el catálogo. */
    fun buscarMedicos(texto: String): List<Medico> =
        medicos.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
            .sortedByDescending { it.calificacion }

    /** Resta de los horarios base las horas ya reservadas para médico y fecha. */
    fun horariosDisponibles(medicoId: Int, fecha: String): List<String> {
        val ocupados = citas.filter {
            it.medicoId == medicoId && it.fecha == fecha && it.estado == "Confirmada"
        }.map { it.hora }
        return horariosBase.filter { it !in ocupados }
    }

    /** Vuelve a comprobar el turno al confirmar y evita reservas duplicadas. */
    fun agendarCita(medicoId: Int, fecha: String, hora: String): Cita? {
        val usuario = usuarioActual ?: return null
        val medico = obtenerMedico(medicoId) ?: return null
        if (fecha.isBlank() || hora !in horariosDisponibles(medicoId, fecha)) return null
        val cita = Cita((citas.maxOfOrNull { it.id } ?: 0) + 1,
            usuario.id, medico.especialidadId, medicoId, fecha, hora)
        citas.add(cita)
        return cita
    }

    /** Separa las citas por paciente y las ordena de la más reciente a la más antigua. */
    fun citasDelUsuario(usuarioId: Int): List<Cita> =
        citas.filter { it.usuarioId == usuarioId }.sortedWith(
            compareByDescending<Cita> { it.fecha }.thenByDescending { it.hora })

    /** Solo el titular puede retirar su cita; al hacerlo libera el horario. */
    fun cancelarCita(citaId: Int): Boolean {
        val usuario = usuarioActual ?: return false
        return citas.removeIf { it.id == citaId && it.usuarioId == usuario.id }
    }
}
