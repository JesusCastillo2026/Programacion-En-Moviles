package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MascotaViewModel(private val repo: VetRepository, private val duenoId: Int, val mascotaId: Int) : ViewModel() {
    val mascota: StateFlow<Mascota?> = (if (mascotaId > 0) repo.observarMascota(mascotaId) else flowOf(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val historial: StateFlow<List<CitaConDetalle>> = (if (mascotaId > 0) repo.observarHistorial(mascotaId) else flowOf(emptyList()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _errores = MutableStateFlow<Map<String, String>>(emptyMap())
    val errores: StateFlow<Map<String, String>> = _errores
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun guardar(nombre: String, especie: String, raza: String, anio: String, peso: String, sexo: String, onExito: () -> Unit) {
        val erroresNuevos = mutableMapOf<String, String>()
        val anioNumero = anio.toIntOrNull()
        val pesoNumero = peso.replace(',', '.').toDoubleOrNull()
        if (nombre.trim().length < 2) erroresNuevos["nombre"] = "Escribe al menos 2 caracteres"
        if (especie !in listOf("PERRO", "GATO", "OTRO")) erroresNuevos["especie"] = "Elige una especie"
        if (sexo !in listOf("MACHO", "HEMBRA")) erroresNuevos["sexo"] = "Elige el sexo"
        if (anioNumero == null || anioNumero !in 1990..LocalDate.now().year) erroresNuevos["anio"] = "Año entre 1990 y ${LocalDate.now().year}"
        if (pesoNumero == null || pesoNumero <= 0) erroresNuevos["peso"] = "Ingresa un peso mayor a cero"
        _errores.value = erroresNuevos
        if (erroresNuevos.isNotEmpty()) return
        viewModelScope.launch {
            val actual = mascota.value
            if (actual != null && actual.duenoId != duenoId) { _mensaje.value = "No puedes editar esta mascota"; return@launch }
            repo.guardarMascota(Mascota(mascotaId, duenoId, nombre.trim(), especie, raza.trim(), anioNumero!!, pesoNumero!!, sexo))
            onExito()
        }
    }

    fun eliminar(onExito: () -> Unit) {
        val actual = mascota.value ?: return
        viewModelScope.launch {
            if (actual.duenoId != duenoId) { _mensaje.value = "No puedes eliminar esta mascota"; return@launch }
            if (repo.mascotaTieneCitas(actual.id)) { _mensaje.value = "No se puede eliminar: tiene citas"; return@launch }
            repo.eliminarMascota(actual)
            onExito()
        }
    }
}
