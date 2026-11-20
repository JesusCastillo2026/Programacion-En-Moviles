package com.tecsup.centrotareas.data

interface TareasRepository {
    suspend fun contarPrimos(hasta: Int, progreso: (Float) -> Unit = {}): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
