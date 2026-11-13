package com.tecsup.cinecastillo.notification

/** Contrato que permite avisar del resultado sin acoplar el ViewModel a Android. */
interface Notificador {
    fun mostrar(titulo: String, texto: String)
}
