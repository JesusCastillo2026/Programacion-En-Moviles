package com.jesuscastillo.miscursos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.jesuscastillo.miscursos.data.AuthRepository

@Composable
fun AuthScreen(repository: AuthRepository) {
    var mostrarRegistro by remember { mutableStateOf(false) }
    var mensajeLogin by remember { mutableStateOf<String?>(null) }
    if (mostrarRegistro) {
        RegistroScreen(
            repository = repository,
            onVolver = { mostrarRegistro = false },
            onCuentaCreada = {
                mensajeLogin = "Cuenta creada. Ya puedes iniciar sesión."
                mostrarRegistro = false
            }
        )
    } else {
        InicioSesionScreen(
            repository = repository,
            onCrearCuenta = { mensajeLogin = null; mostrarRegistro = true },
            mensajeInicial = mensajeLogin
        )
    }
}

@Composable
private fun InicioSesionScreen(
    repository: AuthRepository,
    onCrearCuenta: () -> Unit,
    mensajeInicial: String?
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember(mensajeInicial) { mutableStateOf(mensajeInicial) }
    var cargando by remember { mutableStateOf(false) }

    AuthLayout(titulo = "Bienvenido", subtitulo = "Inicia sesión para ver tus cursos") {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; mensaje = null },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; mensaje = null },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    mensaje = "Escribe tu correo y contraseña."
                } else {
                    cargando = true
                    repository.iniciarSesion(email, password) { error ->
                        cargando = false
                        mensaje = error
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (cargando) CircularProgressIndicator() else Text("Iniciar sesión")
        }
        TextButton(
            onClick = {
                if (email.isBlank()) {
                    mensaje = "Escribe tu correo para enviarte el enlace de recuperación."
                } else {
                    cargando = true
                    repository.enviarRestablecimiento(email) { error ->
                        cargando = false
                        mensaje = error ?: "Enviamos el enlace para restablecer tu contraseña."
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Olvidé mi contraseña") }
        TextButton(onClick = onCrearCuenta, modifier = Modifier.fillMaxWidth()) {
            Text("Crear una cuenta")
        }
    }
}

@Composable
private fun RegistroScreen(
    repository: AuthRepository,
    onVolver: () -> Unit,
    onCuentaCreada: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    AuthLayout(titulo = "Crear cuenta", subtitulo = "Regístrate para guardar tus cursos") {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; mensaje = null },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; mensaje = null },
            label = { Text("Contraseña (mínimo 6 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it; mensaje = null },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                when {
                    email.isBlank() || password.isBlank() || confirmarPassword.isBlank() -> mensaje = "Completa todos los campos."
                    password.length < 6 -> mensaje = "La contraseña debe tener al menos 6 caracteres."
                    password != confirmarPassword -> mensaje = "Las contraseñas no coinciden."
                    else -> {
                        cargando = true
                        repository.registrar(email, password) { error ->
                            cargando = false
                            if (error == null) {
                                onCuentaCreada()
                            } else mensaje = error
                        }
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (cargando) CircularProgressIndicator() else Text("Registrarme")
        }
        TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) { Text("Volver a iniciar sesión") }
    }
}

@Composable
private fun AuthLayout(titulo: String, subtitulo: String, contenido: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(subtitulo, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            contenido()
        }
    }
}
