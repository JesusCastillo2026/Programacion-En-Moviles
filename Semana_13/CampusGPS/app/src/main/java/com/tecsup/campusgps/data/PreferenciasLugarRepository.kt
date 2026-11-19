package com.tecsup.campusgps.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.campusDataStore by preferencesDataStore(name = "campus_preferences")

interface PreferenciasLugarRepository {
    fun observarUltimoLugar(): Flow<Int?>
    suspend fun guardarUltimoLugar(id: Int)
}

class PreferenciasLugarRepositoryDataStore(context: Context) : PreferenciasLugarRepository {
    private val dataStore = context.applicationContext.campusDataStore
    private val lastPlaceKey = intPreferencesKey("ultimo_lugar")

    override fun observarUltimoLugar(): Flow<Int?> =
        dataStore.data.map { preferences -> preferences[lastPlaceKey] }

    override suspend fun guardarUltimoLugar(id: Int) {
        dataStore.edit { preferences -> preferences[lastPlaceKey] = id }
    }
}
