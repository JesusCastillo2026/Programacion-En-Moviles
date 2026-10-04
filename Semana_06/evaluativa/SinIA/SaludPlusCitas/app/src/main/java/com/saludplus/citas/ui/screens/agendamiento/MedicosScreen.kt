package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TarjetaMedico
import com.saludplus.citas.ui.components.TituloSeccion

@Composable
fun MedicosScreen(especialidadId: Int, onElegir: (Int) -> Unit) {
    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    val medicos = Repositorio.medicosPorEspecialidad(especialidadId)
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSeccion(especialidad?.nombre ?: "Especialidad")
        Text("Elige al profesional que prefieras.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (medicos.isEmpty()) item { EstadoVacio("Sin médicos", "No hay profesionales disponibles.") }
            items(medicos, key = { it.id }) { medico ->
                TarjetaMedico(medico) { onElegir(medico.id) }
            }
        }
    }
}
