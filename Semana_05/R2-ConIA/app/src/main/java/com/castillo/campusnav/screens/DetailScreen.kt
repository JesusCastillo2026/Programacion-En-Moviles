package com.castillo.campusnav.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.castillo.campusnav.data.CampusStore
import com.castillo.campusnav.ui.*

/** Un ID inexistente muestra un estado recuperable, nunca un expediente equivocado. */
@Composable
fun DetailScreen(store: CampusStore, itemId: Int, onBack: () -> Unit) {
    val student = store.directory().find { it.id == itemId }
    Page("Expediente académico", onBack) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (student == null) {
                WhiteCard {
                    Text("Perfil no encontrado", style = MaterialTheme.typography.titleLarge)
                    Text("El ID recibido no pertenece al directorio de ejemplo.")
                    Button(onClick = onBack) { Text("Volver al directorio") }
                }
            } else {
                Card(shape = RoundedCornerShape(28.dp)) {
                    Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Ink, Violet))).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Avatar(student.name, student.id == 1, 88.dp)
                        Text(student.name, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                        Text(student.career, color = Color(0xFFE1DDFB))
                        Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
                            Text("PERFIL DE DEMOSTRACIÓN", Modifier.padding(horizontal = 14.dp, vertical = 7.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                WhiteCard {
                    SectionLabel("INFORMACIÓN ACADÉMICA")
                    InfoRow(Icons.Default.Badge, "ID de ejemplo · argumento Int", "DEMO-${student.id.toString().padStart(4, '0')}")
                    HorizontalDivider()
                    InfoRow(Icons.Default.School, "Programa del ejemplo", student.career)
                    HorizontalDivider()
                    InfoRow(Icons.Default.Business, "Área", student.area)
                    HorizontalDivider()
                    InfoRow(Icons.Default.AlternateEmail, "Correo", if (student.id == 1) store.profile.email else "alumno${student.id}@example.com")
                }
                WhiteCard {
                    SectionLabel("ACERCA DEL PERFIL")
                    Text(student.bio.ifBlank { "Sin biografía registrada." })
                }
                val saved = itemId in store.favorites
                Button(onClick = { store.toggleFavorite(itemId) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp)) {
                    Icon(if (saved) Icons.Default.Star else Icons.Default.StarBorder, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (saved) "Quitar de favoritos" else "Guardar en favoritos")
                }
                Text("La navegación recibió el ID $itemId como Int. La ficha se obtiene del directorio local.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
