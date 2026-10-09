package com.saludplus.paciente.ui.screens.notificaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.model.Cita
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.EmptyState
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NotificacionesScreen(onBack: () -> Unit, onOpenAppointment: (String) -> Unit) {
    val citas = Repositorio.citasDelUsuario()
    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Notificaciones", onBack)
        if (citas.isEmpty()) {
            EmptyState("Todo al día", "Cuando agendes una cita, verás aquí sus recordatorios.", Modifier.weight(1f))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(citas, key = { it.id }) { cita ->
                    NotificationItem(cita, onClick = { onOpenAppointment(cita.id) })
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(cita: Cita, onClick: () -> Unit) {
    val medico = Repositorio.obtenerMedico(cita.medicoId)
    val fecha = cita.fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "PE")))
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = AzulClinico)
            Column {
                Text("Tienes una cita programada", fontWeight = FontWeight.Bold)
                Text("${medico?.nombre ?: "Profesional"} · ${fecha.replaceFirstChar { it.uppercase(Locale("es", "PE")) }} a las ${cita.hora}", color = TextoSecundario)
            }
        }
    }
}

