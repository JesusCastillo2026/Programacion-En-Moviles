package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PerfilViewModel(repo: VetRepository, private val prefs: PreferenciasStore, duenoId: Int) : ViewModel() {
    val mascotas: StateFlow<Int> = repo.observarMascotasDeDueno(duenoId).map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val citas: StateFlow<Int> = repo.observarCitasDeDueno(duenoId).map { lista ->
        lista.count { it.cita.estado in listOf("PENDIENTE", "CONFIRMADA") &&
            runCatching { LocalDateTime.parse("${it.cita.fecha}T${it.cita.hora}").isAfter(LocalDateTime.now()) }.getOrDefault(false) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val atenciones: StateFlow<Int> = repo.observarCitasDeDueno(duenoId).map { lista -> lista.count { it.cita.estado == "ATENDIDA" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val notificaciones: StateFlow<Boolean> = prefs.notificaciones.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val aceptarCitas: StateFlow<Boolean> = prefs.aceptarCitas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    fun guardarNotificaciones(valor: Boolean) { viewModelScope.launch { prefs.guardarNotificaciones(valor) } }
    fun guardarAceptarCitas(valor: Boolean) { viewModelScope.launch { prefs.guardarAceptarCitas(valor) } }
}
