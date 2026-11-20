package com.tecsup.centrotareas.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.centrotareas.model.ResultadoGuardado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.historialDataStore by preferencesDataStore(name = "historial_tareas")

class HistorialRepository(private val context: Context) {
    private val key = stringPreferencesKey("ultimos_resultados")

    val resultados: Flow<List<ResultadoGuardado>> = context.historialDataStore.data.map { prefs ->
        prefs[key].orEmpty().lineSequence().filter { it.isNotBlank() }.mapNotNull { fila ->
            val partes = fila.split('|', limit = 3)
            if (partes.size == 3) ResultadoGuardado(partes[0], partes[1], partes[2].toLongOrNull() ?: 0L) else null
        }.toList()
    }

    suspend fun guardar(resultado: ResultadoGuardado) {
        context.historialDataStore.edit { prefs ->
            val actual = prefs[key].orEmpty().lineSequence().filter { it.isNotBlank() }.toList()
            prefs[key] = (listOf("${resultado.categoria}|${resultado.resumen}|${resultado.milisegundos}") + actual).take(5).joinToString("\n")
        }
    }
}
