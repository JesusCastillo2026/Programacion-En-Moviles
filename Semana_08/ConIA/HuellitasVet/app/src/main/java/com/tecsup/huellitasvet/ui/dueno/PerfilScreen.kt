package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.viewmodel.PerfilViewModel
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(nombre: String, correo: String, vm: PerfilViewModel, sesion: SesionViewModel) {
    val mascotas by vm.mascotas.collectAsStateWithLifecycle()
    val citas by vm.citas.collectAsStateWithLifecycle()
    val atenciones by vm.atenciones.collectAsStateWithLifecycle()
    val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
    val notificaciones by vm.notificaciones.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                    androidx.compose.material3.Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.padding(12.dp).size(28.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(correo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Surface(shape = RoundedCornerShape(30.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text("Dueña", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            ResumenPerfil("Mascotas", mascotas.toString(), Modifier.weight(1f), androidx.compose.ui.graphics.Color(0xFFD1FAE5), androidx.compose.ui.graphics.Color(0xFF059669))
            ResumenPerfil("Próximas", citas.toString(), Modifier.weight(1f), androidx.compose.ui.graphics.Color(0xFFFEF3C7), androidx.compose.ui.graphics.Color(0xFFD97706))
            ResumenPerfil("Atenciones", atenciones.toString(), Modifier.weight(1f), androidx.compose.ui.graphics.Color(0xFFDBEAFE), androidx.compose.ui.graphics.Color(0xFF1D4ED8))
        }
        Button(onClick = { sesion.cambiarPerfil() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Column(Modifier.weight(1f)) {
                Text("⇄  Cambiar de perfil", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Modo práctico: ir a vista Veterinario", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f))
            }
        }
        Text("Preferencias", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 5.dp)) {
                PreferenciaRow("Modo oscuro", "Cambia el tema de la app", oscuro) { sesion.guardarModoOscuro(it) }
                PreferenciaRow("Recordatorios", "Avisos de vacunas y controles", notificaciones) { vm.guardarNotificaciones(it) }
            }
        }
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .65f))) {
            Column(Modifier.padding(11.dp)) {
                Text("Tus preferencias se guardan en el dispositivo", fontSize = 12.sp)
                Text("con DataStore", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
        OutlinedButton(
            onClick = { sesion.cerrarSesion() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error, fontSize = 14.sp) }
    }
}

@Composable
private fun ResumenPerfil(titulo: String, valor: String, modifier: Modifier, fondo: androidx.compose.ui.graphics.Color, tinta: androidx.compose.ui.graphics.Color) {
    Card(modifier, shape = RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(containerColor = fondo)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(valor, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = tinta)
            Text(titulo, fontSize = 11.sp, color = tinta, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PreferenciaRow(titulo: String, descripcion: String, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(titulo, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(descripcion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = activo, onCheckedChange = onCambio)
    }
}
