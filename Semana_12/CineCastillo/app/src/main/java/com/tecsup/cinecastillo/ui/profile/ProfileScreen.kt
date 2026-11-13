package com.tecsup.cinecastillo.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.cinecastillo.domain.model.CinemaReservation
import com.tecsup.cinecastillo.domain.model.UserProfile
import java.util.Locale

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onUpdateContact: (String, String) -> Unit = { _, _ -> },
    onClearMessage: () -> Unit = {},
    onJoinMembership: () -> Unit = {}
) {
    var demoMember by rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Text("Mi perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            Surface(Modifier.size(100.dp), shape = RoundedCornerShape(50.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(Icons.Default.Person, contentDescription = "Foto de perfil ilustrativa", modifier = Modifier.padding(22.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
        when {
            state.loading -> item { CircularProgressIndicator() }
            state.profile != null -> {
                item {
                    ProfileDetails(
                        profile = state.profile,
                        state = state,
                        demoMember = demoMember,
                        onUpdateContact = onUpdateContact,
                        onClearMessage = onClearMessage,
                        onJoinMembership = { demoMember = true; onJoinMembership() }
                    )
                }
                if (state.reservations.isEmpty()) {
                    item {
                        SectionCard(title = "Mis entradas") {
                            Text("Aún no tienes reservas guardadas. Cuando confirmes una función, aparecerá en este historial.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    item { Text("Mis entradas", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                    items(state.reservations, key = { it.id }) { reservation -> ReservationCard(reservation) }
                }
            }
            state.error != null -> item { Text(state.error, color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun ProfileDetails(
    profile: UserProfile,
    state: ProfileUiState,
    demoMember: Boolean,
    onUpdateContact: (String, String) -> Unit,
    onClearMessage: () -> Unit,
    onJoinMembership: () -> Unit
) {
    var editing by rememberSaveable(profile.phone, profile.dni) { mutableStateOf(false) }
    var phone by rememberSaveable(profile.phone) { mutableStateOf(profile.phone) }
    var dni by rememberSaveable(profile.dni) { mutableStateOf(profile.dni) }
    var confirmSave by rememberSaveable { mutableStateOf(false) }
    val phoneValid = phone.matches(Regex("^9[0-9]{8}$"))
    val dniValid = dni.matches(Regex("^[0-9]{8}$"))
    val isMember = profile.isMember || demoMember
    LaunchedEffect(state.message) { if (state.message != null) editing = false }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(profile.name.ifBlank { "Usuario Cine Castillo" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        SectionCard(title = "Datos de la cuenta") {
            ProfileLine("Correo", profile.email)
            if (editing) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter(Char::isDigit).take(9) },
                    label = { Text("Teléfono") },
                    supportingText = { Text(if (phoneValid) "Número válido" else "Ingresa 9 dígitos, comenzando con 9") },
                    isError = phone.isNotEmpty() && !phoneValid,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it.filter(Char::isDigit).take(8) },
                    label = { Text("DNI") },
                    supportingText = { Text(if (dniValid) "DNI válido" else "Ingresa 8 dígitos") },
                    isError = dni.isNotEmpty() && !dniValid,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { phone = profile.phone; dni = profile.dni; editing = false }, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(onClick = { confirmSave = true }, enabled = phoneValid && dniValid && !state.saving, modifier = Modifier.weight(1f)) {
                        if (state.saving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Guardar")
                    }
                }
            } else {
                ProfileLine("Teléfono", profile.phone.ifBlank { "No registrado" })
                ProfileLine("DNI", profile.dni.ifBlank { "No registrado" })
                OutlinedButton(onClick = { editing = true; onClearMessage() }, modifier = Modifier.fillMaxWidth()) { Text("Editar teléfono y DNI") }
            }
            state.message?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium) }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
        }
        SectionCard(title = "Socios Cine Castillo") {
            ProfileLine("Estado", if (isMember) "Socio Cine Castillo" else "Aún no eres socio")
            if (!isMember) Button(onClick = onJoinMembership, modifier = Modifier.fillMaxWidth()) { Text("Ver membresía de demostración") }
            else Text("120 puntos de demostración", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text("Los puntos y beneficios se muestran como simulación académica.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (confirmSave) {
        AlertDialog(
            onDismissRequest = { confirmSave = false },
            title = { Text("Confirmar cambios") },
            text = { Text("Se guardarán tu teléfono y DNI en el perfil privado de tu cuenta. ¿Deseas continuar?") },
            confirmButton = {
                TextButton(onClick = { confirmSave = false; onUpdateContact(phone, dni) }) { Text("Guardar datos") }
            },
            dismissButton = { TextButton(onClick = { confirmSave = false }) { Text("Revisar") } }
        )
    }
}

@Composable
private fun ReservationCard(reservation: CinemaReservation) {
    SectionCard(title = reservation.movieTitle) {
        ProfileLine("Sede", reservation.venue)
        ProfileLine("Función", "${reservation.date} · ${reservation.time} · ${reservation.format}")
        ProfileLine("Asientos", reservation.seats.joinToString())
        ProfileLine("Total", "S/ ${"%.2f".format(Locale.US, reservation.total)}")
        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
            Text("Entrada digital · ${reservation.id}", Modifier.fillMaxWidth().padding(12.dp), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Text("Código visual de demostración; no es un boleto válido para una sala real.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh), shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
