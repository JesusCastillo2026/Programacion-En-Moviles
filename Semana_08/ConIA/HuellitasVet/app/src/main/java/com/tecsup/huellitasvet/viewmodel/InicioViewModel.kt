package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class InicioViewModel(repo: VetRepository, duenoId: Int) : ViewModel() {
    val mascotas: StateFlow<List<Mascota>> = repo.observarMascotasDeDueno(duenoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proximaCita: StateFlow<CitaConDetalle?> = repo.observarCitasDeDueno(duenoId)
        .map { citas ->
            val ahora = LocalDateTime.now()
            citas.filter { it.cita.estado in listOf("PENDIENTE", "CONFIRMADA") &&
                LocalDateTime.of(LocalDate.parse(it.cita.fecha), LocalTime.parse(it.cita.hora)).isAfter(ahora) }
                .minWithOrNull(compareBy({ it.cita.fecha }, { it.cita.hora }))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
