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
import kotlinx.coroutines.flow.map
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
    mascotaInicial: Int,
    servicioInicial: Int = 0,
    serviciosIniciales: List<Int> = listOf(servicioInicial).filter { it > 0 }
) : ViewModel() {
    val mascotas: StateFlow<List<Mascota>> = repo.observarMascotasDeDueno(duenoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val fecha = MutableStateFlow(LocalDate.now().toString())
    val mascotaId = MutableStateFlow(mascotaInicial)
    val servicioId = MutableStateFlow(servicioInicial)
    val serviciosSeleccionados = MutableStateFlow(serviciosIniciales.toSet())
    val hora = MutableStateFlow("")
    val nota = MutableStateFlow("")
    val mensaje = MutableStateFlow<String?>(null)
    val aceptarCitas: StateFlow<Boolean> = prefs.aceptarCitas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val horasOcupadas: StateFlow<List<String>> = fecha.flatMapLatest { dia ->
        repo.observarCitasPorFecha(dia).map { citas ->
            citas.filter { it.cita.estado != "CANCELADA" }.flatMap { cita ->
                val minutos = (cita.servicios.ifEmpty { listOf(cita.servicio) }).sumOf { it.duracionMin }.coerceAtLeast(30)
                val inicio = LocalTime.parse(cita.cita.hora)
                generateSequence(inicio) { it.plusMinutes(30) }
                    .takeWhile { it.isBefore(inicio.plusMinutes(minutos.toLong())) }
                    .map { it.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) }
                    .toList()
            }.distinct()
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFecha(valor: String) { fecha.value = valor; hora.value = "" }

    fun alternarServicio(id: Int) {
        val nueva = serviciosSeleccionados.value.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
        serviciosSeleccionados.value = nueva
        servicioId.value = nueva.firstOrNull() ?: 0
        hora.value = ""
    }

    fun horaDisponible(valor: String): Boolean {
        if (valor.isBlank()) return false
        val dia = LocalDate.parse(fecha.value)
        val duracion = servicios.value.filter { it.id in serviciosSeleccionados.value }.sumOf { it.duracionMin }.coerceAtLeast(30)
        val inicio = LocalTime.parse(valor)
        val slots = (duracion + 29) / 30
        val seCruza = (0 until slots).any { paso ->
            inicio.plusMinutes(paso * 30L).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) in horasOcupadas.value
        }
        return valor in Horarios.paraFecha(dia, duracion) && !seCruza &&
            LocalDateTime.of(dia, LocalTime.parse(valor)).isAfter(LocalDateTime.now())
    }

    fun confirmar(onExito: () -> Unit) {
        val mascota = mascotas.value.find { it.id == mascotaId.value }
        val seleccionados = servicios.value.filter { it.id in serviciosSeleccionados.value }
        if (!aceptarCitas.value) { mensaje.value = "La clínica no está recibiendo citas"; return }
        if (mascota == null || seleccionados.isEmpty() || !horaDisponible(hora.value)) {
            mensaje.value = "Elige mascota, servicio y un horario disponible"
            return
        }
        val dia = LocalDate.parse(fecha.value)
        if (dia.isBefore(LocalDate.now()) || dia.isAfter(LocalDate.now().plusDays(7))) {
            mensaje.value = "Fecha fuera de rango"
            return
        }
        viewModelScope.launch {
            runCatching { repo.crearCita(mascota.id, seleccionados.map { it.id }, fecha.value, hora.value, nota.value) }
                .onSuccess { mensaje.value = null; onExito() }
                .onFailure { mensaje.value = it.message ?: "No se pudo registrar la cita" }
        }
    }
}
