package com.tecsup.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.data.HistorialRepository

class TareasViewModelFactory(
    private val repository: TareasRepository,
    private val historialRepository: HistorialRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            return TareasViewModel(repository, historialRepository) as T
        }
        error("ViewModel no reconocido")
    }
}
