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
    private val oscuroClave = booleanPreferencesKey("modoOscuro")
    private val notificacionesClave = booleanPreferencesKey("notificaciones")
    private val citasClave = booleanPreferencesKey("aceptarCitas")

    val perfilActivo: Flow<String> = context.dataStore.data.map { it[perfilClave] ?: "" }
    val usuarioId: Flow<Int> = context.dataStore.data.map { it[usuarioClave] ?: 0 }
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { it[oscuroClave] ?: false }
    val notificaciones: Flow<Boolean> = context.dataStore.data.map { it[notificacionesClave] ?: true }
    val aceptarCitas: Flow<Boolean> = context.dataStore.data.map { it[citasClave] ?: true }

    suspend fun guardarSesion(rol: String, usuarioId: Int) {
        context.dataStore.edit { it[perfilClave] = rol; it[usuarioClave] = usuarioId }
    }

    suspend fun guardarModoOscuro(valor: Boolean) { context.dataStore.edit { it[oscuroClave] = valor } }
    suspend fun guardarNotificaciones(valor: Boolean) { context.dataStore.edit { it[notificacionesClave] = valor } }
    suspend fun guardarAceptarCitas(valor: Boolean) { context.dataStore.edit { it[citasClave] = valor } }
    suspend fun cerrarSesion() { context.dataStore.edit { it.remove(perfilClave); it.remove(usuarioClave) } }
}
