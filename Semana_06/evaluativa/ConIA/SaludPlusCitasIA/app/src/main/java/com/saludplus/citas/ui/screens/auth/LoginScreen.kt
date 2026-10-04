package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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

/** Comprueba las credenciales contra el repositorio y abre la sesión. */
@Composable
fun LoginScreen(onSuccess: () -> Unit, onRegistro: () -> Unit) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        TituloSeccion("Bienvenido de nuevo")
        Spacer(Modifier.height(8.dp))
        Text("Accede a tus citas y resultados.")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(clave, { clave = it }, label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(20.dp))
        BotonPrincipal("Ingresar") {
            if (Repositorio.iniciarSesion(correo, clave) != null) onSuccess()
            else error = "Correo o contraseña incorrectos."
        }
        TextButton(onClick = onRegistro) { Text("Crear una cuenta") }
    }
}
