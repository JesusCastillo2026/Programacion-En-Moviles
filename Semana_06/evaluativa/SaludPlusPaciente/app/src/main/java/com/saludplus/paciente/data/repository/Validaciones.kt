package com.saludplus.paciente.data.repository

import java.time.LocalDate
import com.saludplus.paciente.data.model.RelojClinica
import java.util.Locale

/** Reglas compartidas entre formularios y repositorio; no dependen de la interfaz. */
object Validaciones {
    fun normalizarCorreo(correo: String): String = correo.trim().lowercase(Locale.ROOT)

    /** Valida formato habitual, incluidos correos institucionales; no comprueba que el buzón exista. */
    fun correoValido(correo: String): Boolean {
        val valor = normalizarCorreo(correo)
        if (valor.length > 254 || valor.count { it == '@' } != 1) return false
        val (local, dominio) = valor.split('@')
        if (local.isEmpty() || local.length > 64 || local.startsWith('.') ||
            local.endsWith('.') || ".." in local) return false
        if (!local.matches(Regex("[a-z0-9.!#$%&'*+/=?^_`{|}~-]+"))) return false
        val etiquetas = dominio.split('.')
        return etiquetas.size >= 2 &&
            etiquetas.all { it.matches(Regex("[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?")) } &&
            etiquetas.last().matches(Regex("[a-z]{2,63}"))
    }

    /** Esta versión admite celulares peruanos sin prefijo internacional. */
    fun celularValido(telefono: String): Boolean = telefono.matches(Regex("9[0-9]{8}"))

    fun errorRegistro(nombre: String, correo: String, telefono: String, contrasena: String): String? = when {
        nombre.isBlank() -> "Escribe tu nombre completo."
        !correoValido(correo) -> "Escribe un correo válido, por ejemplo nombre@tecsup.edu.pe."
        !celularValido(telefono) -> "El celular debe tener 9 dígitos y empezar por 9."
        contrasena.isBlank() || contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
        else -> null
    }

    /** Las citas se ofrecen desde hoy y únicamente de lunes a viernes. */
    fun fechaReservable(fecha: LocalDate, hoy: LocalDate = RelojClinica.hoy()): Boolean =
        !fecha.isBefore(hoy) && fecha.dayOfWeek.value in 1..5
}

