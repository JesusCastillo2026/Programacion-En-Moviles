package com.saludplus.paciente.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.theme.CelesteSuave
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
/** Recoge credenciales y presenta el resultado del intento de inicio de sesión. */
fun LoginScreen(onBack: () -> Unit, onLogin: (String, String) -> Boolean, onRegister: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Iniciar sesión", onBack)
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Bienvenido de nuevo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Accede a tus citas y resultados.", color = TextoSecundario)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(correo, { correo = it; error = null }, label = { Text("Correo electrónico") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(contrasena, { contrasena = it; error = null }, label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(20.dp))
            SaludPlusButton("Ingresar", onClick = {
                if (correo.isBlank() || contrasena.isBlank()) error = "Escribe tu correo y contraseña."
                else if (!onLogin(correo, contrasena)) error = "No encontramos una cuenta con esos datos."
            })
            Spacer(Modifier.height(16.dp))
            Text("Demo: demo@saludplus.pe  ·  123456", modifier = Modifier.fillMaxWidth().background(CelesteSuave, RoundedCornerShape(12.dp)).padding(12.dp), color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            androidx.compose.material3.TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Crear una cuenta")
            }
        }
    }
}

