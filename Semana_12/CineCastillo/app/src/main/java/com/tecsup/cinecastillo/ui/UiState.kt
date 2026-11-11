package com.tecsup.cinecastillo.ui
/** Contrato común: la UI representa carga, éxito y error explícitamente. */
sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}
