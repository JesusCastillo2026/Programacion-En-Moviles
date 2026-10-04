package com.saludplus.citas.ui.screens.notificaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TituloSeccion

@Composable
fun NotificacionesScreen(onDetalle: (Int) -> Unit) {
    val usuario = Repositorio.usuarioActual
    val avisos = usuario?.let { Repositorio.citasDelUsuario(it.id) }.orEmpty()
        .map { cita -> cita.id to "Cita con ${Repositorio.obtenerMedico(cita.medicoId)?.nombre.orEmpty()} · ${cita.fecha}" }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TituloSeccion("Avisos de tus citas")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (avisos.isEmpty()) item { EstadoVacio("Sin notificaciones", "Aquí verás tus próximas citas.") }
            items(avisos, key = { it.first }) { aviso ->
                Card(onClick = { onDetalle(aviso.first) }) {
                    Text(aviso.second, modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
            }
        }
    }
}
