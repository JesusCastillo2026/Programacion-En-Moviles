package com.castillo.campusnav.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.castillo.campusnav.R

/** Cada perfil del directorio muestra un avatar propio; el registro del estudiante usa la foto proporcionada. */
@Composable
fun Avatar(name: String, own: Boolean, size: Dp = 52.dp) {
    val image = when {
        own -> R.drawable.jesus_castillo
        name.startsWith("María") -> R.drawable.avatar_maria
        name.startsWith("Carlos") -> R.drawable.avatar_carlos
        name.startsWith("Ana") -> R.drawable.avatar_ana
        name.startsWith("Luis") -> R.drawable.avatar_luis
        name.startsWith("Valeria") -> R.drawable.avatar_valeria
        name.startsWith("Diego") -> R.drawable.avatar_diego
        name.startsWith("Camila") -> R.drawable.avatar_camila
        else -> null
    }
    if (image != null) {
        Image(painterResource(image), contentDescription = "Avatar de $name",
            contentScale = ContentScale.Crop, modifier = Modifier.size(size).clip(CircleShape))
    } else {
        Box(Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Text(name.split(" ").take(2).mapNotNull { it.firstOrNull() }.joinToString(""),
                style = MaterialTheme.typography.titleMedium, color = Violet)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page(title: String, onBack: () -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(title, style = MaterialTheme.typography.titleLarge) }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
        })
    }, content = content)
}

@Composable
fun WhiteCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Violet, modifier = Modifier.size(22.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("¿Cerrar la sesión demo?") },
        text = { Text("Tus favoritos y tu perfil seguirán guardados en este dispositivo.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Cerrar sesión") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
