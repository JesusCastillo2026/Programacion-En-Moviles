package com.tecsup.huellitasvet

import android.app.Application
import com.tecsup.huellitasvet.data.local.DatosIniciales
import com.tecsup.huellitasvet.data.local.VetDatabase
import com.tecsup.huellitasvet.data.repository.VetRepositoryRoom
import com.tecsup.huellitasvet.data.prefs.PreferenciasStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class VetApp : Application() {
    val database by lazy { VetDatabase.obtener(this) }
    val repository by lazy { VetRepositoryRoom(database) }
    val preferencias by lazy { PreferenciasStore(this) }
    val listo = MutableStateFlow(false)

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            DatosIniciales.sembrar(database)
            listo.value = true
        }
    }
}
