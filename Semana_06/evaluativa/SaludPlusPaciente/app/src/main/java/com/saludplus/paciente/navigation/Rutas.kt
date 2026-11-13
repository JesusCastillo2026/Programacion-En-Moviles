package com.saludplus.paciente.navigation

import android.net.Uri

object Rutas {
    const val SPLASH = "splash"
    const val REGISTRO = "registro"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val SEDES = "sedes"
    const val DOCTORES = "doctores"
    const val ESPECIALIDADES = "especialidades"
    const val MEDICOS = "medicos/{especialidadId}"
    const val FECHA_HORA = "fecha-hora/{especialidadId}/{medicoId}"
    const val CONFIRMAR_CITA = "confirmar/{especialidadId}/{medicoId}/{fecha}/{hora}"
    const val CITA_EXITOSA = "cita-exitosa/{citaId}"
    const val MIS_CITAS = "mis-citas"
    const val PERFIL = "perfil"
    const val DETALLE_CITA = "detalle-cita/{citaId}"
    const val RESULTADOS = "resultados"
    const val NOTIFICACIONES = "notificaciones"
    const val TERMINOS = "terminos"

    fun medicos(especialidadId: String) = "medicos/${Uri.encode(especialidadId)}"

    fun fechaHora(especialidadId: String, medicoId: String) =
        "fecha-hora/${Uri.encode(especialidadId)}/${Uri.encode(medicoId)}"

    fun confirmar(especialidadId: String, medicoId: String, fecha: String, hora: String) =
        "confirmar/${Uri.encode(especialidadId)}/${Uri.encode(medicoId)}/${Uri.encode(fecha)}/${Uri.encode(hora)}"

    fun citaExitosa(citaId: String) = "cita-exitosa/${Uri.encode(citaId)}"

    fun detalleCita(citaId: String) = "detalle-cita/${Uri.encode(citaId)}"
}

