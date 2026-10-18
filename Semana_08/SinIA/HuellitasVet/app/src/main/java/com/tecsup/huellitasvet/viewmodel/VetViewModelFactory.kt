package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.huellitasvet.VetApp

class VetViewModelFactory(private val app: VetApp, private val usuarioId: Int = 0) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SesionViewModel::class.java)) {
            return SesionViewModel(app.repository, app.preferencias, app.listo) as T
        }
        if (modelClass.isAssignableFrom(InicioViewModel::class.java)) {
            return InicioViewModel(app.repository, usuarioId) as T
        }
        throw IllegalArgumentException("ViewModel no registrado: ${modelClass.name}")
    }
}
