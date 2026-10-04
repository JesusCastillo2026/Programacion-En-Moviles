package com.saludplus.citas.navigation

/** Nombres de destino y constructores de rutas con parámetros. */
object Rutas {
    const val SPLASH = "splash"
    const val REGISTRO = "registro"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val ESPECIALIDADES = "especialidades"
    const val MEDICOS = "medicos/{especialidadId}"
    const val FECHA_HORA = "fecha/{medicoId}"
    const val CONFIRMAR = "confirmar/{medicoId}/{fecha}/{hora}"
    const val EXITOSA = "exitosa/{citaId}"
    const val CITAS = "citas"
    const val DETALLE = "detalle/{citaId}"
    const val PERFIL = "perfil"
    const val RESULTADOS = "resultados"
    const val NOTIFICACIONES = "notificaciones"
    const val TERMINOS = "terminos"

    fun medicos(especialidadId: Int) = "medicos/$especialidadId"
    fun fechaHora(medicoId: Int) = "fecha/$medicoId"
    fun confirmar(medicoId: Int, fecha: String, hora: String) = "confirmar/$medicoId/$fecha/$hora"
    fun exitosa(citaId: Int) = "exitosa/$citaId"
    fun detalle(citaId: Int) = "detalle/$citaId"
}
