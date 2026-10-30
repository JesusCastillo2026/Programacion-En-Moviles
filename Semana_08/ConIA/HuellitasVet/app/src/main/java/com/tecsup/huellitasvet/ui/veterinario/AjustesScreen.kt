package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.viewmodel.PerfilViewModel
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    nombre: String,
    correo: String,
    vm: PerfilViewModel,
    sesion: SesionViewModel,
    esAdministrador: Boolean = false,
    onGestionarVeterinarios: () -> Unit = {}
) {
    val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
    val notificaciones by vm.notificaciones.collectAsStateWithLifecycle()
    val aceptar by vm.aceptarCitas.collectAsStateWithLifecycle()
    Column(
        Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Icon(Icons.Default.Person, null, Modifier.padding(12.dp).size(28.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text(nombre, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    Text(correo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text(if (esAdministrador) "Administrador" else "Veterinario", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }
        }
        if (esAdministrador) {
            Button(onClick = onGestionarVeterinarios, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Text("Gestionar veterinarios")
            }
        }
        Button(onClick = { sesion.cambiarPerfil() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("⇄  Cambiar de perfil")
        }
        Text("Preferencias", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        PreferenciaVeterinaria(Icons.Default.DarkMode, "Modo oscuro", "Ajusta la apariencia de la app", oscuro) { sesion.guardarModoOscuro(it) }
        PreferenciaVeterinaria(Icons.Default.Notifications, "Notificaciones", "Avisos de nuevas citas", notificaciones) { vm.guardarNotificaciones(it) }
        PreferenciaVeterinaria(Icons.Default.Schedule, "Aceptar citas", "Permite recibir nuevas reservas", aceptar) { vm.guardarAceptarCitas(it) }
        OutlinedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Tus preferencias se guardan en este dispositivo", fontSize = 12.sp)
                Text("con DataStore", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
        OutlinedButton(onClick = { sesion.cerrarSesion() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text("Cerrar sesión", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
    }
}

@Composable
private fun PreferenciaVeterinaria(icono: androidx.compose.ui.graphics.vector.ImageVector, titulo: String, detalle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(titulo, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                Text(detalle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked, onCheckedChange = onChecked)
        }
    }
}
