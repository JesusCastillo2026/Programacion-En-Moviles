package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Horarios
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class PedirCitaViewModel(
    private val repo: VetRepository,
    prefs: PreferenciasStore,
    duenoId: Int,
    mascotaInicial: Int
) : ViewModel() {
    val mascotas: StateFlow<List<Mascota>> = repo.observarMascotasDeDueno(duenoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val fecha = MutableStateFlow(LocalDate.now().toString())
    val mascotaId = MutableStateFlow(mascotaInicial)
    val servicioId = MutableStateFlow(0)
    val hora = MutableStateFlow("")
    val nota = MutableStateFlow("")
    val mensaje = MutableStateFlow<String?>(null)
    val aceptarCitas: StateFlow<Boolean> = prefs.aceptarCitas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val horasOcupadas: StateFlow<List<String>> = fecha.flatMapLatest { repo.observarHorasOcupadas(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFecha(valor: String) { fecha.value = valor; hora.value = "" }

    fun horaDisponible(valor: String): Boolean {
        val dia = LocalDate.parse(fecha.value)
        return valor in Horarios.todos && valor !in horasOcupadas.value &&
            LocalDateTime.of(dia, LocalTime.parse(valor)).isAfter(LocalDateTime.now())
    }

    fun confirmar(onExito: () -> Unit) {
        val mascota = mascotas.value.find { it.id == mascotaId.value }
        val servicio = servicios.value.find { it.id == servicioId.value }
        if (!aceptarCitas.value) { mensaje.value = "La clínica no está recibiendo citas"; return }
        if (mascota == null || servicio == null || !horaDisponible(hora.value)) {
            mensaje.value = "Elige mascota, servicio y un horario disponible"
            return
        }
        val dia = LocalDate.parse(fecha.value)
        if (dia.isBefore(LocalDate.now()) || dia.isAfter(LocalDate.now().plusDays(7))) {
            mensaje.value = "Fecha fuera de rango"
            return
        }
        viewModelScope.launch {
            runCatching { repo.crearCita(mascota.id, servicio.id, fecha.value, hora.value, nota.value) }
                .onSuccess { mensaje.value = null; onExito() }
                .onFailure { mensaje.value = it.message ?: "No se pudo registrar la cita" }
        }
    }
}
