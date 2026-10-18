package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Horarios
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Coordina la selección; la validación definitiva se repite en la transacción del repositorio. */
@OptIn(ExperimentalCoroutinesApi::class)
class ReprogramarCitaViewModel(private val repo: VetRepository, private val duenoId: Int, private val citaId: Int) : ViewModel() {
    val cita = repo.observarCita(citaId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val fecha = MutableStateFlow(LocalDate.now().toString())
    val hora = MutableStateFlow("")
    val mensaje = MutableStateFlow<String?>(null)
    val ocupadas = fecha.flatMapLatest { repo.observarHorasOcupadas(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFecha(valor: String) { fecha.value = valor; hora.value = ""; mensaje.value = null }

    fun disponible(valor: String): Boolean {
        val actual = cita.value?.cita
        val propio = actual?.fecha == fecha.value && actual.hora == valor
        return valor in Horarios.todos && valor !in ocupadas.value && !propio &&
            LocalDateTime.of(LocalDate.parse(fecha.value), LocalTime.parse(valor)).isAfter(LocalDateTime.now())
    }

    fun guardar(onExito: () -> Unit) {
        if (!disponible(hora.value)) { mensaje.value = "Elige un horario libre y futuro"; return }
        viewModelScope.launch {
            runCatching { repo.reprogramarCita(citaId, duenoId, fecha.value, hora.value) }
                .onSuccess { onExito() }
                .onFailure { mensaje.value = it.message ?: "No se pudo reprogramar" }
        }
    }
}
