package com.saludplus.paciente.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Validaciones
import com.saludplus.paciente.ui.components.*

/** Conserva las credenciales mientras se navega y normaliza el correo al iniciar sesión. */
@Composable
fun LoginScreen(onBack: () -> Unit, onLogin: (String, String) -> Boolean,
                onRegister: () -> Unit, cuentaCreada: Boolean = false) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var enviado by rememberSaveable { mutableStateOf(false) }
    AuthLayout("Qué bueno verte.", "Ingresa para consultar tus citas y continuar cuidándote.", onBack) {
        if (cuentaCreada) Text("Cuenta creada. Inicia sesión con tu correo y contraseña.",
            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
        AuthField(correo, { correo = it; error = null }, "Correo electrónico",
            Icons.Default.Email, KeyboardType.Email,
            error = if (enviado && !Validaciones.correoValido(correo)) "Revisa el formato del correo." else null)
        AuthField(contrasena, { contrasena = it; error = null }, "Contraseña",
            Icons.Default.Lock, password = true,
            error = if (enviado && contrasena.isBlank()) "Escribe tu contraseña." else null)
        error?.let { FormError(it) }
        SaludPlusButton("Ingresar", onClick = {
            enviado = true
            error = when {
                !Validaciones.correoValido(correo) -> "Escribe un correo válido."
                contrasena.isBlank() -> "Escribe tu contraseña."
                !onLogin(Validaciones.normalizarCorreo(correo), contrasena) -> "Correo o contraseña incorrectos."
                else -> null
            }
        })
        TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Primera vez? Crear una cuenta")
        }
        HorizontalDivider()
        Text("Cuenta de demostración\ndemo@saludplus.pe · 123456",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

