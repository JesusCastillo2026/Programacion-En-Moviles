package com.castillo.campusnav.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.castillo.campusnav.data.CampusStore
import com.castillo.campusnav.ui.*

/** Resumen con cifras derivadas del directorio y de los favoritos persistidos. */
@Composable
fun HomeScreen(store: CampusStore, onDirectory: () -> Unit, onProfile: () -> Unit, onLogout: () -> Unit) {
    var logout by remember { mutableStateOf(false) }
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("CAMPUS / NAVLAB", style = MaterialTheme.typography.labelLarge, color = Violet)
                    Text("Mi espacio", style = MaterialTheme.typography.headlineMedium)
                }
                IconButton(onClick = onProfile, modifier = Modifier.size(52.dp)) { Avatar(store.profile.name, true, 48.dp) }
            }
            Card(shape = RoundedCornerShape(28.dp)) {
                Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Ink, Color(0xFF6255B8)))).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("BIENVENIDO A TU COMUNIDAD", color = Color(0xFFA8E8D8), style = MaterialTheme.typography.labelMedium)
                    Text("Hola, ${store.profile.name.substringBefore(' ')} 👋", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                    Text("Conecta con nuevas ideas.\nTu siguiente paso empieza aquí.", color = Color(0xFFE3DFF7))
                    FilledTonalButton(onClick = onDirectory) {
                        Text("Explorar comunidad")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Stat("${store.directory().size}", "Alumnos demo", Modifier.weight(1f))
                Stat("${store.favorites.size}", "Favoritos", Modifier.weight(1f))
            }
            Text("¿Qué deseas gestionar hoy?", style = MaterialTheme.typography.titleMedium)
            ActionCard(Icons.Default.Groups, "Directorio de alumnos", "Busca, descubre y guarda contactos", onDirectory)
            ActionCard(Icons.Default.Badge, "Mi perfil académico", "Tu información, a tu manera", onProfile)
            Text("ENTORNO DE APRENDIZAJE", color = Teal, style = MaterialTheme.typography.labelMedium)
            Text("Directorio de ejemplo · Datos guardados solo en este dispositivo.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = { logout = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Cerrar sesión")
            }
        }
    }
    if (logout) LogoutDialog({ logout = false }, { logout = false; onLogout() })
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier) {
    WhiteCard(modifier) {
        Text(value, color = Violet, style = MaterialTheme.typography.headlineMedium)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ActionCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(icon, null, Modifier.padding(12.dp).size(24.dp), tint = Violet)
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Violet)
        }
    }
}
