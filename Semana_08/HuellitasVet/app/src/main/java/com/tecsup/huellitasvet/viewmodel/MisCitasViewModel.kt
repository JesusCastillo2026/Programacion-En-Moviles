package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MisCitasViewModel(private val repo: VetRepository, duenoId: Int) : ViewModel() {
    private val citas = repo.observarCitasDeDueno(duenoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val proximas: StateFlow<List<CitaConDetalle>> = citas.map { lista ->
        lista.filter { it.cita.estado in listOf("PENDIENTE", "CONFIRMADA") && fechaHora(it).isAfter(LocalDateTime.now()) }
            .sortedWith(compareBy({ it.cita.fecha }, { it.cita.hora }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val historial: StateFlow<List<CitaConDetalle>> = citas.map { lista ->
        lista.filterNot { it.cita.estado in listOf("PENDIENTE", "CONFIRMADA") && fechaHora(it).isAfter(LocalDateTime.now()) }
            .sortedWith(compareByDescending<CitaConDetalle> { it.cita.fecha }.thenByDescending { it.cita.hora })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mensaje = MutableStateFlow<String?>(null)

    fun puedeCancelar(detalle: CitaConDetalle): Boolean =
        detalle.cita.estado in listOf("PENDIENTE", "CONFIRMADA") &&
            Duration.between(LocalDateTime.now(), fechaHora(detalle)).toMinutes() > 120

    fun cancelar(detalle: CitaConDetalle) {
        if (!puedeCancelar(detalle)) { mensaje.value = "Ya no puedes cancelar esta cita"; return }
        viewModelScope.launch {
            runCatching { repo.cambiarEstadoCita(detalle.cita.id, EstadoCita.CANCELADA) }
                .onFailure { mensaje.value = it.message ?: "No se pudo cancelar" }
        }
    }

    private fun fechaHora(detalle: CitaConDetalle): LocalDateTime =
        LocalDateTime.of(LocalDate.parse(detalle.cita.fecha), LocalTime.parse(detalle.cita.hora))
}
