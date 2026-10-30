package com.castillo.lab04carritotecsup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import java.util.Locale

fun money(value: Double): String = String.format(Locale.US, "S/ %.2f", value)

/**
 * El Box ancla el DropdownMenu al botón de tres puntos de ESTA tarjeta. Las acciones llegan
 * por callbacks al StoreState, sin guardar una segunda copia del estado dentro de la tarjeta.
 */
@Composable
fun ProductCard(
    producto: Producto,
    favorito: Boolean,
    onFavorite: (Producto) -> Unit,
    onShare: (Producto) -> Unit,
    onReport: (Producto) -> Unit,
    onOrder: (Producto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(producto.categoria.uppercase(), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                    Text(producto.nombre, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text(producto.descripcion, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones de ${producto.nombre}")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(if (favorito) "Quitar de Favoritos" else "Guardar en Favoritos") },
                            onClick = { expanded = false; onFavorite(producto) },
                            leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Compartir") },
                            onClick = { expanded = false; onShare(producto) },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Reportar") },
                            onClick = { expanded = false; onReport(producto) },
                            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) }
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(money(producto.precio), style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text("${producto.cantidad} disponibles", style = MaterialTheme.typography.labelSmall)
            }
            Button(
                onClick = { onOrder(producto) },
                enabled = producto.cantidad > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (producto.cantidad > 0) "Registrar pedido demo" else "Agotado")
            }
        }
    }
}

/** Los reportes de este prototipo se registran localmente; no se envían a un servidor. */
@Composable
fun ReportProductDialog(producto: Producto, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar producto") },
        text = { Text("¿Quieres marcar «${producto.nombre}» para revisión en esta demostración? No se enviará información fuera de la app.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Reportar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
