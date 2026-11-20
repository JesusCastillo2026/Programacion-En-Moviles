package com.tecsup.centrotareas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.coroutineContext

class TareasRepositoryLocal : TareasRepository {
    override suspend fun cargarPerfil(): String {
        delay(2_000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        if (simularError) throw IOException("Sin conexión")
        delay(3_000)
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }

    override suspend fun contarPrimos(hasta: Int, progreso: (Float) -> Unit): Int = withContext(Dispatchers.Default) {
        var cantidad = 0
        for (numero in 2..hasta) {
            coroutineContext.ensureActive()
            if (esPrimo(numero)) cantidad++
            if (numero % maxOf(1, hasta / 100) == 0) progreso(numero.toFloat() / hasta)
        }
        cantidad
    }

    private fun esPrimo(numero: Int): Boolean {
        var divisor = 2
        while (divisor.toLong() * divisor <= numero) {
            if (numero % divisor == 0) return false
            divisor++
        }
        return true
    }
}


