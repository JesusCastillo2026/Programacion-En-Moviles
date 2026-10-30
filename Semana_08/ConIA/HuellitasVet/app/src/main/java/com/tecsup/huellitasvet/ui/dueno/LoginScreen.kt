package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.theme.VetTheme
import com.tecsup.huellitasvet.ui.components.RequisitosContrasenaCard
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun LoginScreen(vm: SesionViewModel) {
    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var esRegistro by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }
    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarRequisitos by remember { mutableStateOf(false) }
    var avisoRegistro by remember { mutableStateOf<String?>(null) }
    var errorLocal by remember { mutableStateOf<String?>(null) }
    val error by vm.error.collectAsStateWithLifecycle()
    val oscuro by vm.modoOscuro.collectAsStateWithLifecycle()

    val rolVisual = if (esRegistro) Rol.CLIENTE else if (rol == Rol.VETERINARIO) Rol.VETERINARIO else Rol.CLIENTE
    VetTheme(rolVisual, oscuro) {
        val coloresCampo = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = .48f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = .78f),
            cursorColor = MaterialTheme.colorScheme.primary
        )
        val coloresOpcion = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .52f),
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 26.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                if (esRegistro) "Crear cuenta" else "Bienvenido",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                if (esRegistro) "Regístrate para cuidar mejor a tus mascotas" else "Ingresa para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = .82f)
            )
            Spacer(Modifier.height(22.dp))

            if (!esRegistro) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = rol == Rol.CLIENTE,
                        onClick = { rol = Rol.CLIENTE; vm.limpiarError(); avisoRegistro = null },
                        label = { Text("Dueño") }, modifier = Modifier.weight(1f),
                        colors = coloresOpcion
                    )
                    FilterChip(
                        selected = rol == Rol.VETERINARIO,
                        onClick = { rol = Rol.VETERINARIO; vm.limpiarError(); avisoRegistro = null },
                        label = { Text("Veterinario") }, modifier = Modifier.weight(1f),
                        colors = coloresOpcion
                    )
                }
                Spacer(Modifier.height(16.dp))
            } else {
                Text("REGISTRO DE CLIENTE", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it; errorLocal = null; vm.limpiarError() },
                    label = { Text("Nombre completo") }, singleLine = true,
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), colors = coloresCampo
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { valor -> telefono = valor.filter(Char::isDigit).take(9); errorLocal = null; vm.limpiarError() },
                    label = { Text("Teléfono (9 dígitos)") }, singleLine = true,
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), colors = coloresCampo
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = distrito, onValueChange = { distrito = it; errorLocal = null; vm.limpiarError() },
                    label = { Text("Distrito") }, singleLine = true,
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), colors = coloresCampo
                )
                Spacer(Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; errorLocal = null; avisoRegistro = null; vm.limpiarError() },
                label = { Text("Correo electrónico") }, singleLine = true,
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), colors = coloresCampo
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it; errorLocal = null; vm.limpiarError() },
                label = { Text("Contraseña") }, singleLine = true,
                visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { claveVisible = !claveVisible }) {
                        Icon(if (claveVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = if (claveVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                },
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().then(
                    if (esRegistro) Modifier.onFocusChanged { if (it.isFocused) mostrarRequisitos = true } else Modifier
                ), colors = coloresCampo
            )

            if (esRegistro) {
                if (mostrarRequisitos) {
                    RequisitosContrasenaCard(clave)
                }
                TextButton(
                    onClick = { mostrarRequisitos = !mostrarRequisitos },
                    modifier = Modifier.align(Alignment.End)
                ) { Text(if (mostrarRequisitos) "Ocultar requisitos" else "Ver requisitos de contraseña") }
                OutlinedTextField(
                    value = confirmarClave, onValueChange = { confirmarClave = it; errorLocal = null; vm.limpiarError() },
                    label = { Text("Confirmar contraseña") }, singleLine = true,
                    visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), colors = coloresCampo
                )
                Spacer(Modifier.height(14.dp))
            } else {
                TextButton(onClick = { mostrarAyuda = true }, modifier = Modifier.align(Alignment.End)) {
                    Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.labelSmall)
                }
            }

            val mensaje = errorLocal ?: error
            if (mensaje != null) {
                Text(mensaje, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (avisoRegistro != null) {
                Text(avisoRegistro.orEmpty(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 10.dp))
            }

            Button(
                onClick = {
                    if (esRegistro) {
                        if (clave != confirmarClave) errorLocal = "Las contraseñas no coinciden"
                        else vm.registrarCliente(nombre, correo, telefono, distrito, clave) {
                            esRegistro = false
                            rol = Rol.CLIENTE
                            clave = ""
                            confirmarClave = ""
                            nombre = ""
                            telefono = ""
                            distrito = ""
                            avisoRegistro = "Cuenta creada exitosamente. Inicia sesión para continuar."
                        }
                    } else vm.login(correo, clave, rol)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text(if (esRegistro) "Crear cuenta" else "Ingresar", fontWeight = FontWeight.SemiBold) }

            if (esRegistro) {
                TextButton(onClick = { esRegistro = false; vm.limpiarError(); errorLocal = null }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            } else {
                if (rol == Rol.CLIENTE) {
                    TextButton(
                        onClick = { esRegistro = true; avisoRegistro = null; errorLocal = null; vm.limpiarError() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("¿No tienes cuenta? Regístrate") }
                }
                if (rol == Rol.CLIENTE) {
                    TextButton(
                        onClick = { correo = "ana@correo.test"; clave = "1234" },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("Usar cuenta de demostración") }
                } else {
                    TextButton(
                        onClick = { correo = "vet@huellitas.test"; clave = "admin" },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("Acceso de veterinario (demo)") }
                    TextButton(
                        onClick = { correo = "camila.rojas@huellitas.test"; clave = "VetHuellitas2026!" },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("Acceso de administrador (demo)") }
                }
            }
        }

        if (mostrarAyuda) AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text("Recuperar acceso") },
            text = { Text("Esta versión de demostración usa cuentas locales. Selecciona ‘Usar cuenta de demostración’ para completar el acceso.") },
            confirmButton = { TextButton(onClick = { mostrarAyuda = false }) { Text("Entendido") } }
        )

    }
}
