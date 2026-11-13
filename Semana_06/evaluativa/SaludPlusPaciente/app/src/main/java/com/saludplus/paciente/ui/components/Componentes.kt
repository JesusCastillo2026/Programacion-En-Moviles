package com.saludplus.paciente.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.CelesteSuave
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
@OptIn(ExperimentalMaterial3Api::class)
/** Barra superior uniforme con título y acción para volver. */
fun AppBackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = AzulClinico
        )
    )
}

@Composable
/** Encabezado reutilizable para el título y texto de apoyo de cada vista. */
fun PageHeading(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        }
    }
}

@Composable
/** Botón principal con altura y color consistentes en toda la experiencia. */
fun SaludPlusButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AzulClinico)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
/** Campo de búsqueda compartido por los catálogos. */
fun SearchField(value: String, onValueChange: (String) -> Unit, hint: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = TextoSecundario) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextoSecundario) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulClinico,
            unfocusedBorderColor = Color(0xFFDCE4EF),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
/** Avatar de iniciales de respaldo cuando no se carga un retrato ilustrativo. */
fun PlaceholderAvatar(nombre: String, size: Dp = 54.dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(CelesteSuave),
        contentAlignment = Alignment.Center
    ) {
        if (nombre.isBlank()) {
            Icon(Icons.Default.Person, contentDescription = null, tint = AzulClinico)
        } else {
            Text(nombre.trim().split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                color = AzulClinico, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
/** Mensaje centrado que explica una lista vacía y cómo continuar. */
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, contentDescription = null, tint = AzulClinico, modifier = Modifier.size(40.dp))
        Text(title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(message, color = TextoSecundario, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
        if (actionLabel != null && onAction != null) SaludPlusButton(actionLabel, onAction)
    }
}

@Composable
/** Contenedor blanco de bordes suaves para agrupar información relacionada. */
fun SoftCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        content()
    }
}

@Composable
/** Etiqueta visual para mostrar el estado de una cita. */
fun StatusChip(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(50)).background(CelesteSuave).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(text, color = AzulClinico, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

