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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

private data class ReglaClave(val titulo: String, val cumplida: Boolean)

private fun reglasClave(clave: String) = listOf(
    ReglaClave("Al menos 8 caracteres", clave.length >= 8),
    ReglaClave("Una letra mayúscula", clave.any(Char::isUpperCase)),
    ReglaClave("Una letra minúscula", clave.any(Char::isLowerCase)),
    ReglaClave("Un número", clave.any(Char::isDigit)),
    ReglaClave("Un símbolo (por ejemplo: !, @ o #)", clave.any { !it.isLetterOrDigit() && !it.isWhitespace() }),
    ReglaClave("Sin espacios", clave.none(Char::isWhitespace))
)

@Composable
fun LoginScreen(vm: SesionViewModel) {
    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var esRegistro by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
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
    val reglas = reglasClave(clave)

    VetTheme(if (esRegistro) Rol.CLIENTE else rol, oscuro) {
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(22.dp))

            if (!esRegistro) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = rol == Rol.CLIENTE,
                        onClick = { rol = Rol.CLIENTE; vm.limpiarError(); avisoRegistro = null },
                        label = { Text("Dueño") }, modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = rol == Rol.ADMIN,
                        onClick = { rol = Rol.ADMIN; vm.limpiarError(); avisoRegistro = null },
                        label = { Text("Veterinario") }, modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(16.dp))
            } else {
                Text("REGISTRO DE CLIENTE", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it; errorLocal = null; vm.limpiarError() },
                    label = { Text("Nombre completo") }, singleLine = true,
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; errorLocal = null; avisoRegistro = null; vm.limpiarError() },
                label = { Text("Correo electrónico") }, singleLine = true,
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
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
                )
            )

            if (esRegistro) {
                TextButton(onClick = { mostrarRequisitos = true }, modifier = Modifier.align(Alignment.End)) {
                    Text("Ver requisitos de contraseña")
                }
                OutlinedTextField(
                    value = confirmarClave, onValueChange = { confirmarClave = it; errorLocal = null; vm.limpiarError() },
                    label = { Text("Confirmar contraseña") }, singleLine = true,
                    visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
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
                        else vm.registrarCliente(nombre, correo, clave) {
                            esRegistro = false
                            rol = Rol.CLIENTE
                            clave = ""
                            confirmarClave = ""
                            nombre = ""
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
                TextButton(
                    onClick = {
                        if (rol == Rol.CLIENTE) { correo = "ana@correo.test"; clave = "1234" }
                        else { correo = "vet@huellitas.test"; clave = "admin" }
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text("Usar cuenta de demostración") }
            }
        }

        if (mostrarAyuda) AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text("Recuperar acceso") },
            text = { Text("Esta versión de demostración usa cuentas locales. Selecciona ‘Usar cuenta de demostración’ para completar el acceso.") },
            confirmButton = { TextButton(onClick = { mostrarAyuda = false }) { Text("Entendido") } }
        )

        if (mostrarRequisitos && esRegistro) AlertDialog(
            onDismissRequest = { mostrarRequisitos = false },
            title = { Text("Requisitos de contraseña") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("Para proteger tu cuenta, incluye todos estos elementos:", style = MaterialTheme.typography.bodyMedium)
                    reglas.forEach { regla ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Icon(
                                imageVector = if (regla.cumplida) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (regla.cumplida) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(regla.titulo, color = if (regla.cumplida) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { mostrarRequisitos = false }) { Text("Entendido") } }
        )
    }
}
