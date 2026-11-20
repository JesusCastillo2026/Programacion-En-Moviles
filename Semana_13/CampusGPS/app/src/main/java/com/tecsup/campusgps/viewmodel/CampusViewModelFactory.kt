package com.tecsup.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.campusgps.data.UbicacionRepository
import com.tecsup.campusgps.data.LugaresRepository
import com.tecsup.campusgps.data.PreferenciasLugarRepository
import com.tecsup.campusgps.data.RutasRepository

class CampusViewModelFactory(
    private val ubicacionRepository: UbicacionRepository,
    private val lugaresRepository: LugaresRepository,
    private val preferenciasRepository: PreferenciasLugarRepository,
    private val rutasRepository: RutasRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            return MapaViewModel(ubicacionRepository, lugaresRepository, preferenciasRepository, rutasRepository) as T
        }
        error("ViewModel desconocido")
    }
}
