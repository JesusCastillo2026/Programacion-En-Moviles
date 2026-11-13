package com.castillo.campusnav.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.castillo.campusnav.data.*
import com.castillo.campusnav.ui.*

/** Borrador separado del perfil guardado: cancelar no modifica la persistencia. */
@Composable
fun ProfileScreen(store: CampusStore, onBack: () -> Unit, onLogout: () -> Unit) {
    var editing by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf(store.profile.name) }
    var email by rememberSaveable { mutableStateOf(store.profile.email) }
    var phone by rememberSaveable { mutableStateOf(store.profile.phone) }
    var bio by rememberSaveable { mutableStateOf(store.profile.bio) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var saved by rememberSaveable { mutableStateOf(false) }
    var logout by remember { mutableStateOf(false) }
    Page("Mi perfil académico", onBack) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            WhiteCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Avatar(store.profile.name, true, 76.dp)
                    Column(Modifier.weight(1f)) {
                        Text(store.profile.name, style = MaterialTheme.typography.titleLarge)
                        Text("Programación en Móviles", color = Violet)
                    }
                }
                Text("Tu perfil, siempre contigo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (saved) Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                Text("✓ Cambios guardados en este dispositivo", Modifier.fillMaxWidth().padding(16.dp), color = Teal)
            }
            WhiteCard {
                SectionLabel(if (editing) "EDITAR INFORMACIÓN PERSONAL" else "INFORMACIÓN PERSONAL")
                if (editing) {
                    OutlinedTextField(name, { name = it; error = null }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(email, { email = it; error = null }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    OutlinedTextField(phone, { phone = it; error = null }, label = { Text("Teléfono · opcional") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    OutlinedTextField(bio, { bio = it; error = null }, label = { Text("Sobre mí") }, modifier = Modifier.fillMaxWidth(), minLines = 3, supportingText = { Text("${bio.length}/240") })
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(onClick = {
                        val draft = Profile(name, email, phone, bio)
                        error = validateProfile(draft)
                        if (error == null) { store.saveProfile(draft); editing = false; saved = true }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Guardar cambios") }
                    TextButton(onClick = { editing = false; error = null }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
                } else {
                    InfoRow(Icons.Default.Person, "Nombre", store.profile.name)
                    HorizontalDivider()
                    InfoRow(Icons.Default.AlternateEmail, "Correo", store.profile.email)
                    HorizontalDivider()
                    InfoRow(Icons.Default.Phone, "Teléfono", store.profile.phone.ifBlank { "Sin registrar" })
                    HorizontalDivider()
                    Text(store.profile.bio.ifBlank { "Sin biografía registrada." })
                    OutlinedButton(onClick = {
                        name = store.profile.name; email = store.profile.email; phone = store.profile.phone; bio = store.profile.bio
                        saved = false; error = null; editing = true
                    }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Edit, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Editar mi perfil") }
                }
            }
            WhiteCard {
                SectionLabel("ESPACIO ACADÉMICO")
                InfoRow(Icons.Default.School, "Laboratorio", "05 · Navegación entre pantallas")
                InfoRow(Icons.Default.Code, "Tecnologías", "Kotlin · Compose · Navigation")
                InfoRow(Icons.Default.Star, "Favoritos guardados", "${store.favorites.size} perfiles")
            }
            Text("Los cambios son locales. Este prototipo no modifica registros académicos reales.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = { logout = true }, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
        }
    }
    if (logout) LogoutDialog({ logout = false }, { logout = false; onLogout() })
}
