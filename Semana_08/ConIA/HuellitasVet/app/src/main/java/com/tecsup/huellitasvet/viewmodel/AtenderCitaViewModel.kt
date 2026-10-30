package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AtenderCitaViewModel(private val repo: VetRepository, private val citaId: Int) : ViewModel() {
    val cita: StateFlow<CitaConDetalle?> = repo.observarCita(citaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val mensaje = MutableStateFlow<String?>(null)

    fun registrar(diagnostico: String, tratamiento: String, peso: String, onExito: () -> Unit) {
        val numero = peso.replace(',', '.').toDoubleOrNull()
        if (diagnostico.trim().length < 5) { mensaje.value = "El diagnóstico debe tener al menos 5 caracteres"; return }
        if (numero == null || numero <= 0) { mensaje.value = "El peso debe ser mayor que cero"; return }
        if (cita.value?.cita?.estado !in listOf("PENDIENTE", "CONFIRMADA")) { mensaje.value = "Esta cita ya está cerrada"; return }
        viewModelScope.launch {
            runCatching { repo.registrarAtencion(citaId, diagnostico, tratamiento, numero) }
                .onSuccess { onExito() }
                .onFailure { mensaje.value = it.message }
        }
    }

    fun cancelar(onExito: () -> Unit) {
        viewModelScope.launch {
            runCatching { repo.cambiarEstadoCita(citaId, EstadoCita.CANCELADA) }
                .onSuccess { onExito() }
                .onFailure { mensaje.value = it.message }
        }
    }
}
