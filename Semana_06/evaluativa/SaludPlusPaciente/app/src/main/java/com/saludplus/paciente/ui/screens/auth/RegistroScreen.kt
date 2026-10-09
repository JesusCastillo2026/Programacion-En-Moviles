package com.saludplus.paciente.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.saludplus.paciente.data.repository.Validaciones
import com.saludplus.paciente.ui.components.*

/** El estado guardable conserva el formulario al consultar Términos y regresar. */
@Composable
fun RegistroScreen(
    onBack: () -> Unit,
    onRegister: (String, String, String, String) -> Boolean,
    onLogin: () -> Unit,
    onTerms: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var enviado by rememberSaveable { mutableStateOf(false) }

    AuthLayout("Empieza a cuidarte.", "Crea tu cuenta y organiza tus citas en un solo lugar.", onBack) {
        AuthField(nombre, { nombre = it; error = null }, "Nombre completo", Icons.Default.Person,
            error = if (enviado && nombre.isBlank()) "Escribe tu nombre." else null)
        AuthField(correo, { correo = it; error = null }, "Correo electrónico",
            Icons.Default.Email, KeyboardType.Email,
            error = if (enviado && !Validaciones.correoValido(correo)) "Usa un correo como nombre@tecsup.edu.pe." else null)
        // Se aceptan dígitos ASCII; la regla de nueve cifras se aplica también en el repositorio.
        AuthField(telefono, { telefono = it.filter { c -> c in '0'..'9' }; error = null },
            "Celular peruano", Icons.Default.Phone, KeyboardType.Phone,
            error = if (enviado && !Validaciones.celularValido(telefono)) "Debe tener 9 dígitos y empezar por 9." else null,
            hint = "9 dígitos, sin +51")
        AuthField(contrasena, { contrasena = it; error = null }, "Contraseña",
            Icons.Default.Lock, password = true,
            error = if (enviado && (contrasena.isBlank() || contrasena.length < 6)) "Usa al menos 6 caracteres." else null)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it; error = null })
            Column {
                Text("He leído y acepto", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onTerms) { Text("Términos y condiciones") }
            }
        }
        error?.let { FormError(it) }
        SaludPlusButton("Crear cuenta", onClick = {
            enviado = true
            error = Validaciones.errorRegistro(nombre, correo, telefono, contrasena)
            if (error == null && !aceptaTerminos) error = "Acepta los términos para continuar."
            if (error == null && !onRegister(nombre.trim(), Validaciones.normalizarCorreo(correo), telefono, contrasena)) {
                error = "Ese correo ya está registrado. Puedes iniciar sesión."
            }
        })
        TextButton(onClick = onLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

