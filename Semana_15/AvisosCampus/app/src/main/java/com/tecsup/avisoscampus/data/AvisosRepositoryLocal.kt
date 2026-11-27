package com.tecsup.avisoscampus.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.json.JSONArray
import org.json.JSONObject

private val Context.avisosDataStore by preferencesDataStore(name = "historial_avisos")

class AvisosRepositoryLocal(context: Context) : AvisosRepository {
    private val store = context.applicationContext.avisosDataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val historialKey = stringPreferencesKey("avisos_json")

    override val avisos: StateFlow<List<Aviso>> = store.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferencias -> decodificar(preferencias[historialKey].orEmpty()) }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun registrar(aviso: Aviso) {
        store.edit { preferencias ->
            val anteriores = decodificar(preferencias[historialKey].orEmpty())
            preferencias[historialKey] = codificar((listOf(aviso) + anteriores).take(1_000))
        }
    }

    override suspend fun limpiarHistorial() {
        store.edit { preferencias -> preferencias[historialKey] = "[]" }
    }

    private fun codificar(avisos: List<Aviso>): String = JSONArray().apply {
        avisos.forEach { aviso ->
            put(JSONObject().apply {
                put("id", aviso.id)
                put("titulo", aviso.titulo)
                put("texto", aviso.texto)
                put("origen", aviso.origen.name)
                put("hora", aviso.hora)
                put("urgente", aviso.urgente)
            })
        }
    }.toString()

    private fun decodificar(json: String): List<Aviso> = runCatching {
        val arreglo = JSONArray(json.ifBlank { "[]" })
        List(arreglo.length()) { indice ->
            val aviso = arreglo.getJSONObject(indice)
            Aviso(
                id = aviso.getInt("id"),
                titulo = aviso.getString("titulo"),
                texto = aviso.getString("texto"),
                origen = Origen.valueOf(aviso.getString("origen")),
                hora = aviso.getString("hora"),
                urgente = aviso.optBoolean("urgente", false)
            )
        }
    }.getOrDefault(emptyList())
}
