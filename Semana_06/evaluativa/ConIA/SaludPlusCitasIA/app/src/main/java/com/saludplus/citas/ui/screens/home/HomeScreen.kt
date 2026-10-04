package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TituloSeccion
import com.saludplus.citas.ui.theme.AzulProfundo
import com.saludplus.citas.ui.theme.Turquesa

/** Inicio ConIA: jerarquía visual clara, accesos y especialidades en carrusel. */
@Composable
fun HomeScreen(
    onEspecialidades: () -> Unit,
    onEspecialidad: (Int) -> Unit,
    onCitas: () -> Unit,
    onNotificaciones: () -> Unit
) {
    val nombre = Repositorio.usuarioActual?.nombre?.substringBefore(" ") ?: "Paciente"
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Hola, $nombre 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Tu espacio de salud, en un solo lugar")
            }
            TextButton(onClick = onNotificaciones) { Text("🔔") }
        }
        // El encabezado con gradiente distingue esta versión del diseño básico.
        Box(Modifier.fillMaxWidth().background(
            Brush.linearGradient(listOf(AzulProfundo, MaterialTheme.colorScheme.primary, Turquesa)),
            RoundedCornerShape(24.dp)
        )) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("CUIDAMOS DE TI", style = MaterialTheme.typography.labelLarge, color = Color.White)
                Text("Tu bienestar empieza aquí", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text("Especialistas de confianza y citas a tu ritmo.", color = Color.White)
                FilledTonalButton(onClick = onEspecialidades) { Text("Agendar una cita  →") }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text("${Repositorio.especialidades.size}", style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Especialidades")
                }
            }
            Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Text("${Repositorio.medicos.size}", style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Text("Profesionales")
                }
            }
        }
        TituloSeccion("Especialidades destacadas")
        // LazyRow mantiene compacto el catálogo y permite explorar sin salir del inicio.
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(Repositorio.especialidadesDestacadas(), key = { it.id }) { especialidad ->
                Card(onClick = { onEspecialidad(especialidad.id) }, modifier = Modifier.width(158.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(especialidad.icono, style = MaterialTheme.typography.headlineMedium)
                        Text(especialidad.nombre, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        TextButton(onClick = onEspecialidades) { Text("Ver todas las especialidades →") }
        TituloSeccion("Accesos rápidos")
        BotonPrincipal("Explorar especialistas", onClick = onEspecialidades)
        OutlinedButton(onClick = onCitas, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mis citas")
        }
    }
}
