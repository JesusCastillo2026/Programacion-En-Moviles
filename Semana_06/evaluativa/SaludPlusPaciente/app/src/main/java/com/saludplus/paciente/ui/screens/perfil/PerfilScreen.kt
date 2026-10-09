package com.saludplus.paciente.ui.screens.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.PlaceholderAvatar
import com.saludplus.paciente.ui.components.SoftCard
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
fun PerfilScreen(onLogout: () -> Unit) {
    var confirmarCierre by remember { mutableStateOf(false) }
    val usuario = Repositorio.usuarioActual
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        PageHeading("Mi perfil", "Administra tu información")
        SoftCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PlaceholderAvatar(usuario?.nombre.orEmpty(), 64.dp)
                Column {
                    Text(usuario?.nombre ?: "Paciente", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(usuario?.correo.orEmpty(), color = TextoSecundario)
                    Text(usuario?.telefono.orEmpty(), color = TextoSecundario)
                }
            }
        }
        SoftCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Cuenta del paciente", fontWeight = FontWeight.Bold)
                Text("Tus datos se guardan en memoria durante esta sesión de demostración.", color = TextoSecundario)
            }
        }
        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = { confirmarCierre = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Text("  Cerrar sesión")
        }
    }
    if (confirmarCierre) {
        AlertDialog(
            onDismissRequest = { confirmarCierre = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Volverás a la pantalla de bienvenida.") },
            confirmButton = { TextButton(onClick = { confirmarCierre = false; onLogout() }) { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { confirmarCierre = false }) { Text("Cancelar") } }
        )
    }
}

