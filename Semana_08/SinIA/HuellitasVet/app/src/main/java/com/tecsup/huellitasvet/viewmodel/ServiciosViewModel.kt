package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiciosViewModel(private val repo: VetRepository) : ViewModel() {
    val servicios: StateFlow<List<Servicio>> = repo.observarServicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mensaje = MutableStateFlow<String?>(null)

    fun alternar(servicio: Servicio) {
        viewModelScope.launch { repo.cambiarActivo(servicio.id, !servicio.activo) }
    }

    fun guardar(servicio: Servicio, onExito: () -> Unit) {
        if (servicio.nombre.isBlank() || servicio.precio <= 0 || servicio.duracionMin !in listOf(15, 30, 45, 60)) {
            mensaje.value = "Completa nombre, duración y precio válido"
            return
        }
        viewModelScope.launch {
            runCatching { repo.guardarServicio(servicio) }
                .onSuccess { mensaje.value = null; onExito() }
                .onFailure { mensaje.value = it.message }
        }
    }

    fun eliminar(servicio: Servicio) {
        viewModelScope.launch {
            if (repo.servicioTieneCitas(servicio.id)) { mensaje.value = "Tiene citas: desactiva el servicio en lugar de eliminarlo"; return@launch }
            runCatching { repo.eliminarServicio(servicio) }.onFailure { mensaje.value = it.message }
        }
    }
}
