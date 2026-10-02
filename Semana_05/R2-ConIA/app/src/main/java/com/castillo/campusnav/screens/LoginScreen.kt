package com.castillo.campusnav.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.castillo.campusnav.data.validDemoLogin
import com.castillo.campusnav.ui.*

/** Acceso exclusivamente demostrativo. La contraseña permanece solo en memoria. */
@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var help by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Ink, Color(0xFF4E449B))))) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.School, null, tint = Color(0xFFA8E8D8), modifier = Modifier.size(32.dp))
                Text("CAMPUS / NAVLAB", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tu comunidad.\nMás cerca.", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                Text("Un espacio para conectar, explorar\ny construir tu próximo paso.", color = Color(0xFFDAD7F3), style = MaterialTheme.typography.bodyLarge)
            }
            WhiteCard {
                Text("Portal académico", style = MaterialTheme.typography.titleLarge)
                Text("Accede a la experiencia de demostración.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(email, { email = it; error = false }, label = { Text("Correo demo") },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, null) }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(password, { password = it; error = false }, label = { Text("Contraseña demo") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) }, singleLine = true,
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { visible = !visible }) { Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, if (visible) "Ocultar contraseña" else "Mostrar contraseña") } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
                if (error) Text("Usa las credenciales de demostración indicadas abajo.", color = MaterialTheme.colorScheme.error)
                Button(onClick = { if (validDemoLogin(email, password)) { password = ""; onLogin() } else error = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) { Text("Iniciar sesión") }
                TextButton(onClick = { help = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("¿Cómo ingreso?") }
                HorizontalDivider()
                Text("PRUEBA SIN CUENTA REAL", style = MaterialTheme.typography.labelMedium, color = Teal)
                Text("demo@campus.test  ·  NavLab2026", style = MaterialTheme.typography.bodySmall)
                OutlinedButton(onClick = { password = ""; onLogin() }, modifier = Modifier.fillMaxWidth()) { Text("Explorar demo") }
            }
            Text("Laboratorio 05 · Jetpack Compose\nSin conexión a servicios de Tecsup.", color = Color(0xFFDAD7F3), style = MaterialTheme.typography.bodySmall)
        }
    }
    if (help) AlertDialog(onDismissRequest = { help = false }, title = { Text("Acceso de demostración") },
        text = { Text("Usa demo@campus.test y NavLab2026, o pulsa Explorar demo. No ingreses contraseñas personales: no se crean cuentas ni se recuperan claves reales.") },
        confirmButton = { TextButton(onClick = { help = false }) { Text("Entendido") } })
}
