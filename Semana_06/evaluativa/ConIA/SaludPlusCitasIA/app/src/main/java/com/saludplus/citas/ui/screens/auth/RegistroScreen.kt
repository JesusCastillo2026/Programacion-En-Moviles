package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TituloSeccion

/** Formulario con validaciones antes de guardar un nuevo paciente en memoria. */
@Composable
fun RegistroScreen(onSuccess: () -> Unit, onLogin: () -> Unit, onTerminos: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var acepta by rememberSaveable { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Spacer(Modifier.height(24.dp))
        TituloSeccion("Crea tu cuenta")
        Text("Regístrate para agendar tus citas médicas.")
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(clave, { clave = it }, label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        OutlinedTextField(confirmar, { confirmar = it }, label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = acepta, onCheckedChange = { acepta = it })
            Text("Acepto los términos y condiciones")
        }
        TextButton(onClick = onTerminos) { Text("Leer términos") }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        BotonPrincipal("Registrarme") {
            // Validamos primero el formulario; solo después agregamos el usuario al repositorio.
            error = when {
                nombre.isBlank() -> "Ingresa tu nombre."
                !correo.contains("@") -> "Ingresa un correo válido."
                clave.length < 6 -> "La contraseña debe tener 6 caracteres como mínimo."
                clave != confirmar -> "Las contraseñas no coinciden."
                !acepta -> "Debes aceptar los términos."
                Repositorio.registrarUsuario(nombre, correo, clave) == null ->
                    "El correo ya está registrado."
                else -> ""
            }
            if (error.isEmpty()) onSuccess()
        }
        TextButton(onClick = onLogin) { Text("¿Ya tienes cuenta? Inicia sesión") }
    }
}
