package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun LoginScreen(vm: SesionViewModel) {
    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    val error by vm.error.collectAsStateWithLifecycle()

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🐾", style = MaterialTheme.typography.displayLarge)
        Text("HuellitasVet", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Cuidamos a quienes más quieres", style = MaterialTheme.typography.bodyMedium)
        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = rol == Rol.CLIENTE, onClick = { rol = Rol.CLIENTE; vm.limpiarError() }, label = { Text("Dueño") })
            FilterChip(selected = rol == Rol.ADMIN, onClick = { rol = Rol.ADMIN; vm.limpiarError() }, label = { Text("Veterinario") })
        }
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(clave, { clave = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
        if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
        Button(onClick = { vm.login(correo, clave, rol) }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Ingresar") }
        TextButton(onClick = {
            if (rol == Rol.CLIENTE) { correo = "ana@correo.test"; clave = "1234" }
            else { correo = "vet@huellitas.test"; clave = "admin" }
        }) { Text("Usar cuenta de demostración") }
    }
}
