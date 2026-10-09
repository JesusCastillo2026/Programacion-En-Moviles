package com.saludplus.paciente.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.model.Sede
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.SoftCard
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.components.recordarHoraActual
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.AzulProfundo
import com.saludplus.paciente.ui.theme.CelesteSuave
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
/** Resume la actividad del paciente y enlaza con las secciones principales. */
fun HomeScreen(
    onSedes: () -> Unit,
    onSede: (String) -> Unit,
    onDoctors: () -> Unit,
    onResults: () -> Unit,
    onNotifications: () -> Unit
) {
    val fuenteGrande = LocalDensity.current.fontScale > 1.3f
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Clínica SaludPlus", style = MaterialTheme.typography.titleMedium, color = AzulClinico, fontWeight = FontWeight.Bold)
                Text("Hola, ${Repositorio.usuarioActual?.nombre?.substringBefore(' ') ?: "Paciente"} 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onNotifications) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notificaciones", tint = AzulClinico)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = AzulClinico)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(AzulProfundo, AzulClinico))).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.18f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("TU BIENESTAR, PRIMERO", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                Text("Cuida tu salud\ncon un plan simple", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Elige dónde atenderte y luego encuentra a tu especialista.", color = Color.White.copy(alpha = 0.88f))
                Button(
                    onClick = onSedes,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AzulProfundo)
                ) {
                    Text("Elegir sede y agendar", modifier = Modifier.padding(vertical = 8.dp), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        PageHeading("Accesos rápidos", "Empieza por una sede o conoce a nuestros doctores")
        BoxWithConstraints(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            if (maxWidth < 340.dp || fuenteGrande) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShortcutCard("Sedes", "Elige dónde atenderte", Icons.Default.CalendarMonth, onSedes, Modifier.fillMaxWidth())
                    ShortcutCard("Doctores", "Filtra por especialidad y mira sus fotos", Icons.Default.Favorite, onDoctors, Modifier.fillMaxWidth())
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShortcutCard("Sedes", "Elige dónde atenderte", Icons.Default.CalendarMonth, onSedes, Modifier.weight(1f))
                    ShortcutCard("Doctores", "Profesionales por especialidad", Icons.Default.Favorite, onDoctors, Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        PageHeading("SEDES", "Selecciona un local para solicitar tu cita")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp, bottom = 26.dp)) {
            items(Repositorio.sedes, key = { it.id }) { sede ->
                SedeCard(sede) { onSede(sede.id) }
            }
        }
        PageHeading("Doctores", "Conoce a nuestros profesionales por especialidad")
        SaludPlusButton("Ver directorio de doctores", onClick = onDoctors,
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp))
        androidx.compose.material3.TextButton(onClick = onResults, modifier = Modifier.padding(bottom = 24.dp)) {
            Text("Consultar resultados")
        }
    }
}

@Composable
private fun ShortcutCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = AzulClinico, modifier = Modifier.size(22.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SedeCard(sede: Sede, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(156.dp).heightIn(min = 132.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(CelesteSuave), contentAlignment = Alignment.Center) {
                Text("+", color = AzulClinico, fontWeight = FontWeight.Bold)
            }
            Text(sede.nombre, fontWeight = FontWeight.SemiBold)
            Text(sede.distrito, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
        }
    }
}

