package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TituloSeccion

@Composable
fun ConfirmarCitaScreen(medicoId: Int, fecha: String, hora: String, onConfirmar: (Int) -> Unit) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }
    var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        TituloSeccion("Confirma tu cita")
        Card {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Especialidad: ${especialidad?.nombre ?: "No disponible"}")
                Text("Médico: ${medico?.nombre ?: "No disponible"}")
                Text("Fecha: $fecha")
                Text("Hora: $hora")
                Text("Paciente: ${Repositorio.usuarioActual?.nombre ?: "No disponible"}")
            }
        }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        BotonPrincipal("Confirmar cita", habilitado = medico != null) {
            val cita = Repositorio.agendarCita(medicoId, fecha, hora)
            if (cita == null) error = "Este horario ya no está disponible."
            else onConfirmar(cita.id)
        }
    }
}
