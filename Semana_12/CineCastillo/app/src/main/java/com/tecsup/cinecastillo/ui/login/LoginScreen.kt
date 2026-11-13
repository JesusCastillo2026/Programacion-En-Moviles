package com.tecsup.cinecastillo.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.tecsup.cinecastillo.R

@Composable
fun LoginScreen(
    busy: Boolean,
    error: String?,
    notice: String?,
    passwordResetBusy: Boolean,
    passwordResetFeedback: String?,
    passwordResetSuccess: Boolean?,
    onLogin: (String, String) -> Unit,
    onOpenRegister: () -> Unit,
    onRequestPasswordReset: (String) -> Unit,
    onClearPasswordResetFeedback: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var resetEmail by rememberSaveable { mutableStateOf("") }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    AuthScaffold(title = "Tu próxima gran historia") {
        Surface(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp
        ) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Bienvenido de nuevo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                if (notice != null) Text(notice, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Correo electrónico") }, singleLine = true, shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Contraseña") }, singleLine = true, shape = RoundedCornerShape(16.dp), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                TextButton(
                    onClick = {
                        resetEmail = email
                        onClearPasswordResetFeedback()
                        showResetDialog = true
                    },
                    enabled = !busy,
                    modifier = Modifier.align(Alignment.End)
                ) { Text("¿Te olvidaste tu contraseña?") }
                if (error != null) Text(error, Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                Button(onClick = { onLogin(email, password) }, enabled = !busy, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(54.dp)) {
                    if (busy) CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp) else Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onOpenRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("¿No tienes cuenta? Regístrate") }
            }
        }
        Text("Las cuentas nuevas se crean con perfil de usuario.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { if (!passwordResetBusy) showResetDialog = false },
            title = { Text("Recuperar contraseña") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Escribe el correo de tu cuenta y te enviaremos un enlace para crear una contraseña nueva.")
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !passwordResetBusy
                    )
                    if (passwordResetFeedback != null) {
                        Text(
                            passwordResetFeedback,
                            color = if (passwordResetSuccess == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onRequestPasswordReset(resetEmail) },
                    enabled = !passwordResetBusy && passwordResetSuccess != true
                ) {
                    if (passwordResetBusy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text(if (passwordResetSuccess == true) "Enlace enviado" else "Enviar enlace")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false; onClearPasswordResetFeedback() }, enabled = !passwordResetBusy) {
                    Text(if (passwordResetSuccess == true) "Listo" else "Cancelar")
                }
            }
        )
    }
}

@Composable
fun RegisterScreen(
    busy: Boolean,
    error: String?,
    onRegister: (String, String, String, String, String, String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var dni by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmation by rememberSaveable { mutableStateOf("") }
    var passwordFocused by rememberSaveable { mutableStateOf(false) }
    AuthScaffold(title = "Crea tu cuenta") {
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Únete a Cine Castillo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Nombre completo") }, singleLine = true, shape = RoundedCornerShape(16.dp))
                OutlinedTextField(phone, { phone = it.filter(Char::isDigit).take(9) }, Modifier.fillMaxWidth(), label = { Text("Teléfono") }, singleLine = true, shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                OutlinedTextField(dni, { dni = it.filter(Char::isDigit).take(8) }, Modifier.fillMaxWidth(), label = { Text("DNI") }, singleLine = true, shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Correo electrónico") }, singleLine = true, shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                Box {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().onFocusChanged { passwordFocused = it.isFocused },
                        label = { Text("Contraseña") }, singleLine = true, shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    if (passwordFocused) {
                        Popup(alignment = Alignment.TopStart, offset = IntOffset(0, -184), properties = PopupProperties(focusable = false)) {
                            Surface(Modifier.widthIn(min = 250.dp, max = 340.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh, tonalElevation = 8.dp) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                    Text("Tu contraseña debe incluir", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    PasswordRule("Al menos 8 caracteres", password.length >= 8)
                                    PasswordRule("Una letra", password.any(Char::isLetter))
                                    PasswordRule("Un número", password.any(Char::isDigit))
                                    PasswordRule("Un carácter especial", password.any { !it.isLetterOrDigit() })
                                }
                            }
                        }
                    }
                }
                OutlinedTextField(confirmation, { confirmation = it }, Modifier.fillMaxWidth(), label = { Text("Confirma tu contraseña") }, singleLine = true, shape = RoundedCornerShape(16.dp), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                if (error != null) Text(error, Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                Button(onClick = { onRegister(name, phone, dni, email, password, confirmation) }, enabled = !busy, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(54.dp)) {
                    if (busy) CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp) else Text("Crear cuenta", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onBackToLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Ya tengo una cuenta · Iniciar sesión") }
            }
        }
    }
}

@Composable
private fun PasswordRule(label: String, passed: Boolean) {
    val color = if (passed) Color(0xFF43C58A) else MaterialTheme.colorScheme.error
    Text("${if (passed) "✓" else "×"}  $label", color = color, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun AuthScaffold(title: String, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).imePadding(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 460.dp).verticalScroll(rememberScrollState()).padding(horizontal = 26.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(painterResource(R.drawable.cine_castillo_logo), "Logo de Cine Castillo", Modifier.size(132.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Descubre películas y guarda las historias que te gustan.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            content()
            Text("CINE · ESTRENOS · GRANDES HISTORIAS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}
