package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.model.DiaAgenda
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModel(private val repo: VetRepository) : ViewModel() {
    val fechaSeleccionada = MutableStateFlow(LocalDate.now().toString())
    val mes = MutableStateFlow(YearMonth.now())
    /** Room vuelve a emitir los conteos cuando una cita se confirma, cancela o reprograma. */
    val calendario: StateFlow<List<DiaAgenda>> = mes.flatMapLatest {
        repo.observarAgendaMensual(it.atDay(1).toString(), it.plusMonths(1).atDay(1).toString())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun moverMes(pasos: Long) { mes.value = mes.value.plusMonths(pasos) }
    fun seleccionarDia(dia: LocalDate) { fechaSeleccionada.value = dia.toString() }
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
