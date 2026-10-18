package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.huellitasvet.VetApp

class VetViewModelFactory(private val app: VetApp, private val usuarioId: Int = 0, private val itemId: Int = 0) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SesionViewModel::class.java)) {
            return SesionViewModel(app.repository, app.preferencias, app.listo) as T
        }
        if (modelClass.isAssignableFrom(InicioViewModel::class.java)) {
            return InicioViewModel(app.repository, usuarioId) as T
        }
        if (modelClass.isAssignableFrom(MascotaViewModel::class.java)) {
            return MascotaViewModel(app.repository, usuarioId, itemId) as T
        }
        if (modelClass.isAssignableFrom(PedirCitaViewModel::class.java)) {
            return PedirCitaViewModel(app.repository, app.preferencias, usuarioId, itemId) as T
        }
        if (modelClass.isAssignableFrom(MisCitasViewModel::class.java)) {
            return MisCitasViewModel(app.repository, usuarioId) as T
        }
        throw IllegalArgumentException("ViewModel no registrado: ${modelClass.name}")
    }
}
