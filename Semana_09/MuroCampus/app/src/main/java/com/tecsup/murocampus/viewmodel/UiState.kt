package com.tecsup.murocampus.viewmodel

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}

fun mensajeDe(error: Throwable): String = when (error) {
    is IOException -> "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."
    is HttpException -> "Error del servidor (${error.code()}). Inténtalo de nuevo."
    is SerializationException -> "La respuesta del servidor no tiene el formato esperado."
    else -> "Ocurrió un error. Inténtalo de nuevo."
}
