package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TituloSeccion

@Composable
fun HomeScreen(
    onEspecialidades: () -> Unit,
    onEspecialidad: (Int) -> Unit,
    onCitas: () -> Unit,
    onNotificaciones: () -> Unit
) {
    val nombre = Repositorio.usuarioActual?.nombre?.substringBefore(" ") ?: "Paciente"
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Hola, $nombre", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("¿Cómo podemos ayudarte hoy?")
            }
            TextButton(onClick = onNotificaciones) { Text("🔔") }
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tu bienestar es primero", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                Text("Encuentra especialistas y agenda tu cita en pocos pasos.",
                    color = MaterialTheme.colorScheme.onPrimary)
                FilledTonalButton(onClick = onEspecialidades) { Text("Agendar una cita") }
            }
        }
        TituloSeccion("Especialidades destacadas")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(Repositorio.especialidadesDestacadas(), key = { it.id }) { especialidad ->
                Card(onClick = { onEspecialidad(especialidad.id) }, modifier = Modifier.width(150.dp),
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
