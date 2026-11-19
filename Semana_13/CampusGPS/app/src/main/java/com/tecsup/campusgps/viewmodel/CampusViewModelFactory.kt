package com.tecsup.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.campusgps.data.UbicacionRepository
import com.tecsup.campusgps.data.LugaresRepository
import com.tecsup.campusgps.data.PreferenciasLugarRepository

class CampusViewModelFactory(
    private val ubicacionRepository: UbicacionRepository,
    private val lugaresRepository: LugaresRepository,
    private val preferenciasRepository: PreferenciasLugarRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            return MapaViewModel(ubicacionRepository, lugaresRepository, preferenciasRepository) as T
        }
        error("ViewModel desconocido")
    }
}
