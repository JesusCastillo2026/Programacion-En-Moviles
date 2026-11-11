package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.DatoGrafico
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class PanelViewModel(repo: VetRepository) : ViewModel() {
    private val inicioMes = LocalDate.now().withDayOfMonth(1).toString()
    val citasHoy: StateFlow<Int> = repo.observarCitasDeFecha(LocalDate.now().toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val pendientes: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoCita.PENDIENTE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val atendidas: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoCita.ATENDIDA)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val ingresosMes: StateFlow<Double> = repo.observarIngresosDesde(inicioMes)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        EstadoCita.entries.map { estado -> repo.observarCantidadPorEstado(estado) }
    ) { cantidades -> EstadoCita.entries.mapIndexed { indice, estado -> DatoGrafico(estado.name, cantidades[indice].toFloat()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val porServicio: StateFlow<List<DatoGrafico>> = repo.observarCitasPorServicio()
        .map { datos -> datos.map { DatoGrafico(it.nombre, it.cantidad.toFloat()) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
