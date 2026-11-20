package com.tecsup.centrotareas.model

sealed interface PrimosUi {
    data object Inicial : PrimosUi
    data object Calculando : PrimosUi
    data class Listo(val cantidad: Int, val milisegundos: Long) : PrimosUi
    data class Progreso(val valor: Float) : PrimosUi
    data object Cancelado : PrimosUi
}

data class ResultadoGuardado(val categoria: String, val resumen: String, val milisegundos: Long)

data class EventoActividad(val titulo: String, val detalle: String)

enum class EstadoProceso { EN_ESPERA, EN_CURSO, COMPLETADO, CANCELADO, ERROR }

data class ProcesoConcurrenteUi(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val estado: EstadoProceso = EstadoProceso.EN_ESPERA,
    val progreso: Float? = 0f,
    val tiempoMs: Long? = null,
    val resultado: String? = null
)

sealed interface CargaUi {
    data object Inicial : CargaUi
    data object Cargando : CargaUi
    data class Exito(
        val modo: String,
        val perfil: String,
        val pedidos: Int,
        val milisegundos: Long
    ) : CargaUi
    data class Error(val mensaje: String) : CargaUi
}
