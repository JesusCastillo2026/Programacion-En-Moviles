package com.tecsup.huellitasvet.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("ajustes")

class PreferenciasStore(private val context: Context) {
    private val perfilClave = stringPreferencesKey("perfilActivo")
    private val usuarioClave = intPreferencesKey("usuarioId")
    private val notificacionesClave = booleanPreferencesKey("notificaciones")
    private val citasClave = booleanPreferencesKey("aceptarCitas")

    val perfilActivo: Flow<String> = context.dataStore.data.map { it[perfilClave] ?: "" }
    val usuarioId: Flow<Int> = context.dataStore.data.map { it[usuarioClave] ?: 0 }
    fun modoOscuro(usuarioId: Int): Flow<Boolean> = context.dataStore.data.map { preferencias ->
        if (usuarioId <= 0) false else preferencias[booleanPreferencesKey("modoOscuro_usuario_$usuarioId")] ?: false
    }
    val notificaciones: Flow<Boolean> = context.dataStore.data.map { it[notificacionesClave] ?: true }
    val aceptarCitas: Flow<Boolean> = context.dataStore.data.map { it[citasClave] ?: true }

    suspend fun guardarSesion(rol: String, usuarioId: Int) {
        context.dataStore.edit { it[perfilClave] = rol; it[usuarioClave] = usuarioId }
    }

    suspend fun guardarModoOscuro(usuarioId: Int, valor: Boolean) {
        if (usuarioId <= 0) return
        context.dataStore.edit { it[booleanPreferencesKey("modoOscuro_usuario_$usuarioId")] = valor }
    }
    suspend fun guardarNotificaciones(valor: Boolean) { context.dataStore.edit { it[notificacionesClave] = valor } }
    suspend fun guardarAceptarCitas(valor: Boolean) { context.dataStore.edit { it[citasClave] = valor } }
    suspend fun cerrarSesion() { context.dataStore.edit { it.remove(perfilClave); it.remove(usuarioClave) } }
}
