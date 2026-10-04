package com.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TituloSeccion

/** Permite revisar y cancelar una cita propia con confirmación previa. */
@Composable
fun DetalleCitaScreen(citaId: Int, onVolver: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    var confirmarCancelacion by remember { mutableStateOf(false) }
    if (cita == null || cita.usuarioId != Repositorio.usuarioActual?.id) {
        EstadoVacio("Cita no encontrada", "Vuelve a Mis citas.")
        return
    }
    val medico = Repositorio.obtenerMedico(cita.medicoId)
    val especialidad = Repositorio.obtenerEspecialidad(cita.especialidadId)
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TituloSeccion("Detalle de la cita")
        Card {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Especialidad: ${especialidad?.nombre.orEmpty()}")
                Text("Médico: ${medico?.nombre.orEmpty()}")
                Text("Fecha: ${cita.fecha}")
                Text("Hora: ${cita.hora}")
                Text("Estado: ${cita.estado}")
            }
        }
        BotonPrincipal("Cancelar cita") { confirmarCancelacion = true }
    }
    if (confirmarCancelacion) AlertDialog(
        onDismissRequest = { confirmarCancelacion = false },
        title = { Text("Cancelar cita") },
        text = { Text("¿Deseas liberar este horario?") },
        confirmButton = {
            TextButton(onClick = {
                Repositorio.cancelarCita(citaId)
                confirmarCancelacion = false
                onVolver()
            }) { Text("Sí, cancelar") }
        },
        dismissButton = {
            TextButton(onClick = { confirmarCancelacion = false }) { Text("Conservar cita") }
        }
    )
}
