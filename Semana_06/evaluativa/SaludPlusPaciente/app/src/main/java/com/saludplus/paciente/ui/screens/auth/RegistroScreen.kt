package com.saludplus.paciente.ui.screens.auth

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
/** Valida los campos básicos y envía una nueva cuenta al repositorio en memoria. */
fun RegistroScreen(
    onBack: () -> Unit,
    onRegister: (String, String, String, String) -> Boolean,
    onLogin: () -> Unit,
    onTerms: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Crear cuenta", onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Regístrate como paciente", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Completa tus datos para organizar tus citas.", color = TextoSecundario)
            OutlinedTextField(nombre, { nombre = it; error = null }, label = { Text("Nombre completo") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            OutlinedTextField(telefono, { telefono = it; error = null }, label = { Text("Teléfono") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            OutlinedTextField(correo, { correo = it; error = null }, label = { Text("Correo electrónico") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            OutlinedTextField(contrasena, { contrasena = it; error = null }, label = { Text("Contraseña (mínimo 6 caracteres)") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it })
                Text("Acepto los ", color = TextoSecundario)
                TextButton(onClick = onTerms) { Text("términos y condiciones", color = AzulClinico) }
            }
            if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            SaludPlusButton("Crear cuenta", onClick = {
                error = when {
                    nombre.isBlank() || telefono.isBlank() -> "Completa tu nombre y teléfono."
                    !correo.contains("@") || !correo.substringAfter("@", "").contains(".") -> "Escribe un correo válido."
                    contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
                    !aceptaTerminos -> "Debes aceptar los términos para continuar."
                    else -> null
                }
                if (error == null && !onRegister(nombre, correo, telefono, contrasena)) {
                    error = "Ese correo ya está registrado. Inicia sesión o usa otro correo."
                }
            })
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta?", color = TextoSecundario)
                TextButton(onClick = onLogin) { Text("Inicia sesión") }
            }
        }
    }
}

