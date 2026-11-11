package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.MascotaConDueno
import com.tecsup.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PacienteVista(val detalle: MascotaConDueno, val ultimaVisita: String?)

class PacientesViewModel(repo: VetRepository) : ViewModel() {
    val busqueda = MutableStateFlow("")
    val especie = MutableStateFlow("")
    val pacientes: StateFlow<List<PacienteVista>> = combine(
        repo.observarTodasLasMascotas(), repo.observarUltimasVisitas(), busqueda, especie
    ) { mascotas, visitas, texto, especieElegida ->
        val porId = visitas.associateBy { it.mascotaId }
        mascotas.filter { item ->
            (especieElegida.isEmpty() || item.mascota.especie == especieElegida) &&
                (item.mascota.nombre.contains(texto, ignoreCase = true) || item.dueno.nombre.contains(texto, ignoreCase = true))
        }.map { PacienteVista(it, porId[it.mascota.id]?.fecha) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
