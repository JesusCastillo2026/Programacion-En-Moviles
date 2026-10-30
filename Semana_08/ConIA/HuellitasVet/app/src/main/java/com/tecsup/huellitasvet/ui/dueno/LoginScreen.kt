package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.theme.VetTheme
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun LoginScreen(vm: SesionViewModel) {
    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mostrarAyuda by remember { mutableStateOf(false) }
    val error by vm.error.collectAsStateWithLifecycle()
    val oscuro by vm.modoOscuro.collectAsStateWithLifecycle()

    VetTheme(rol, oscuro) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.weight(.65f))
            Text("Bienvenido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Ingresa para continuar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(rol == Rol.CLIENTE, { rol = Rol.CLIENTE; vm.limpiarError() }, label = { Text("Dueño") }, modifier = Modifier.weight(1f))
                FilterChip(rol == Rol.ADMIN, { rol = Rol.ADMIN; vm.limpiarError() }, label = { Text("Veterinario") }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = correo, onValueChange = { correo = it }, label = { Text("Correo") },
                singleLine = true, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = clave, onValueChange = { clave = it }, label = { Text("Contraseña") },
                singleLine = true, visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = { mostrarAyuda = true }, modifier = Modifier.align(Alignment.End)) {
                Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.labelSmall)
            }
            if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            Button(
                onClick = { vm.login(correo, clave, rol) }, modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Ingresar", fontWeight = FontWeight.SemiBold) }
            TextButton(
                onClick = {
                    if (rol == Rol.CLIENTE) { correo = "ana@correo.test"; clave = "1234" }
                    else { correo = "vet@huellitas.test"; clave = "admin" }
                }, modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("Usar cuenta de demostración") }
            Spacer(Modifier.weight(1f))
        }
        if (mostrarAyuda) AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text("Recuperar acceso") },
            text = { Text("Esta versión de demostración usa cuentas locales. Selecciona ‘Usar cuenta de demostración’ para completar el acceso.") },
            confirmButton = { TextButton(onClick = { mostrarAyuda = false }) { Text("Entendido") } }
        )
    }
}
