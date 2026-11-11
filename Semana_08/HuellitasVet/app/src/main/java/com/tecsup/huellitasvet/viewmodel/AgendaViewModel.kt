package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModel(private val repo: VetRepository) : ViewModel() {
    val fechaSeleccionada = MutableStateFlow(LocalDate.now().toString())
    val citas: StateFlow<List<CitaConDetalle>> = fechaSeleccionada.flatMapLatest { repo.observarCitasPorFecha(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mensaje = MutableStateFlow<String?>(null)

    fun confirmar(detalle: CitaConDetalle) {
        if (detalle.cita.estado != EstadoCita.PENDIENTE.name) { mensaje.value = "Solo se confirman citas pendientes"; return }
        viewModelScope.launch { runCatching { repo.cambiarEstadoCita(detalle.cita.id, EstadoCita.CONFIRMADA) }.onFailure { mensaje.value = it.message } }
    }
    fun cancelar(detalle: CitaConDetalle) {
        if (detalle.cita.estado !in listOf("PENDIENTE", "CONFIRMADA")) { mensaje.value = "Cita cerrada"; return }
        viewModelScope.launch { runCatching { repo.cambiarEstadoCita(detalle.cita.id, EstadoCita.CANCELADA) }.onFailure { mensaje.value = it.message } }
    }
}
