package com.jesuscastillo.miscursos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesuscastillo.miscursos.data.AuthRepository

private val LoginPurple = Color(0xFF6650A4)
private val Ink = Color(0xFF202124)

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

    AuthLayout(
        encabezado = {
            Text(
                "Programación Móvil · TECSUP",
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            TecsupWordmark()
        },
        contenido = {
            Text(
                "Iniciar Sesión 🔐",
                color = Ink,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(22.dp))
            AuthField(
                value = email,
                onValueChange = { email = it; mensaje = null },
                label = "Correo electrónico",
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(10.dp))
            AuthField(
                value = password,
                onValueChange = { password = it; mensaje = null },
                label = "Contraseña",
                secret = true
            )
            mensaje?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(18.dp))
            PrimaryAuthButton(
                texto = "Ingresar",
                cargando = cargando,
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        mensaje = "Escribe tu correo y contraseña."
                    } else {
                        cargando = true
                        repository.iniciarSesion(email.trim(), password) { error ->
                            cargando = false
                            mensaje = error
                        }
                    }
                }
            )
            TextButton(
                onClick = {
                    if (email.isBlank()) {
                        mensaje = "Escribe tu correo para enviarte el enlace de recuperación."
                    } else {
                        cargando = true
                        repository.enviarRestablecimiento(email.trim()) { error ->
                            cargando = false
                            mensaje = error ?: "Enviamos el enlace para restablecer tu contraseña."
                        }
                    }
                },
                enabled = !cargando,
                modifier = Modifier.fillMaxWidth()
            ) { Text("¿Olvidaste tu contraseña?", color = LoginPurple) }
            TextButton(onClick = onCrearCuenta, modifier = Modifier.fillMaxWidth()) {
                Text("¿No tienes cuenta? Regístrate aquí", color = LoginPurple, fontWeight = FontWeight.Medium)
            }
        },
        pie = {
            Text(
                "Jesus Castillo S. - TECSUP",
                color = Ink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    )
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

    AuthLayout(
        encabezado = {},
        contenido = {
            Text(
                "Crear cuenta ✨",
                color = Ink,
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            AuthField(
                value = email,
                onValueChange = { email = it; mensaje = null },
                label = "Correo electrónico",
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(10.dp))
            AuthField(
                value = password,
                onValueChange = { password = it; mensaje = null },
                label = "Contraseña (mínimo 6 caracteres)",
                secret = true
            )
            Spacer(Modifier.height(10.dp))
            AuthField(
                value = confirmarPassword,
                onValueChange = { confirmarPassword = it; mensaje = null },
                label = "Confirmar contraseña",
                secret = true
            )
            mensaje?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(18.dp))
            PrimaryAuthButton(
                texto = "Registrarme",
                cargando = cargando,
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() || confirmarPassword.isBlank() -> mensaje = "Completa todos los campos."
                        password.length < 6 -> mensaje = "La contraseña debe tener al menos 6 caracteres."
                        password != confirmarPassword -> mensaje = "Las contraseñas no coinciden."
                        else -> {
                            cargando = true
                            repository.registrar(email.trim(), password) { error ->
                                cargando = false
                                if (error == null) onCuentaCreada() else mensaje = error
                            }
                        }
                    }
                }
            )
            TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("← Volver al login", color = LoginPurple)
            }
        }
    )
}

@Composable
private fun AuthLayout(
    encabezado: @Composable () -> Unit,
    contenido: @Composable () -> Unit,
    pie: (@Composable () -> Unit)? = null
) {
    Surface(color = Color.White) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                encabezado()
                Column(modifier = Modifier.fillMaxWidth()) { contenido() }
                pie?.let {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { it() }
                }
            }
        }
    }
}

@Composable
private fun TecsupWordmark() {
    Image(
        painter = painterResource(com.jesuscastillo.miscursos.R.drawable.tecsup_logo),
        contentDescription = "Logo de TECSUP",
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxWidth().height(58.dp)
    )
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    secret: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (secret) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LoginPurple,
            focusedLabelColor = LoginPurple,
            cursorColor = LoginPurple
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PrimaryAuthButton(texto: String, cargando: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LoginPurple),
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        if (cargando) CircularProgressIndicator(color = Color.White) else Text(texto, fontWeight = FontWeight.SemiBold)
    }
}
