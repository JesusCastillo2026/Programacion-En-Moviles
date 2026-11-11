package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(nombre, style = MaterialTheme.typography.titleLarge); Text(correo); Text("Dueño") } }
        Text("$mascotas mascotas · $citas citas próximas · $atenciones atenciones")
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Modo oscuro", Modifier.weight(1f)); Switch(oscuro, { sesion.guardarModoOscuro(it) }) }
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Recordatorios", Modifier.weight(1f)); Switch(notificaciones, { vm.guardarNotificaciones(it) }) }
        Button(onClick = { sesion.cambiarPerfil() }, modifier = Modifier.fillMaxWidth()) { Text("Cambiar de perfil") }
        TextButton(onClick = { sesion.cerrarSesion() }) { Text("Cerrar sesión") }
    }
}
