package com.castillo.lab04carritotecsup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val categories = listOf("Todos", "Tecnología", "Estudio", "Accesorios")

/** Catálogo: LazyRow de categorías y LazyColumn de productos; ambos reaccionan al filtro. */
@Composable
fun CatalogScreen(
    state: StoreState,
    padding: PaddingValues,
    onFavorite: (Producto) -> Unit,
    onShare: (Producto) -> Unit,
    onReport: (Producto) -> Unit,
    onOrder: (Producto) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Todos") }
    val visible = state.productos.filter { product ->
        (category == "Todos" || product.categoria == category) &&
            (query.isBlank() || product.nombre.contains(query.trim(), ignoreCase = true))
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DESCUBRE TU TIENDA", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary)
                    Text("Encuentra algo para hoy", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Text("Explora, guarda favoritos y registra pedidos de demostración.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar producto") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = { category = option },
                        label = { Text(option) }
                    )
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Productos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${visible.size} disponibles", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (visible.isEmpty()) {
            item { EmptyState("No encontramos productos", "Prueba otra búsqueda o categoría.") }
        } else {
            items(visible, key = { it.id }) { producto ->
                ProductCard(producto, state.esFavorito(producto.id), onFavorite, onShare, onReport, onOrder)
            }
        }
    }
}

/** Esta pantalla usa exactamente los mismos identificadores que el badge del drawer. */
@Composable
fun FavoritesScreen(
    state: StoreState,
    padding: PaddingValues,
    onFavorite: (Producto) -> Unit,
    onShare: (Producto) -> Unit,
    onReport: (Producto) -> Unit,
    onOrder: (Producto) -> Unit
) {
    val favoriteProducts = state.productos.filter { it.id in state.favoritos }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Tus favoritos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${favoriteProducts.size} productos guardados en esta sesión",
                style = MaterialTheme.typography.bodyMedium)
        }
        if (favoriteProducts.isEmpty()) {
            item { EmptyState("Aún no tienes favoritos", "Abre ⋮ en una tarjeta y toca Guardar en Favoritos.") }
        } else {
            items(favoriteProducts, key = { it.id }) { producto ->
                ProductCard(producto, true, onFavorite, onShare, onReport, onOrder)
            }
        }
    }
}

/** Los pedidos son una simulación en memoria y muestran el precio al momento de registrarlos. */
@Composable
fun OrdersScreen(state: StoreState, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mis pedidos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Práctica local · No se realiza ningún cobro ni envío.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("Total demo: ${money(state.pedidos.sumOf { it.precio })}",
                style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
        if (state.pedidos.isEmpty()) {
            item { EmptyState("No hay pedidos", "Desde Inicio puedes registrar un pedido de prueba.") }
        } else {
            items(state.pedidos, key = { it.numero }) { pedido ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pedido #${pedido.numero}", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Text(pedido.producto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(money(pedido.precio), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(state: StoreState, padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(20.dp)) {
                Text("Jesús Castillo Sumire", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Estudiante · TECSUP", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text("Resumen de esta sesión", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("${state.productos.size} productos en catálogo")
        Text("${state.totalFavoritos} favoritos")
        Text("${state.pedidos.size} pedidos de prueba")
        Text("Los cambios se reinician al cerrar la aplicación; esta versión no usa servidor ni base de datos.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyState(title: String, detail: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(detail, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Conserva el formulario de productos del trabajo base, ahora con validación visible. */
@Composable
fun AddProductDialog(onDismiss: () -> Unit, onSave: (String, String, String, String) -> String?) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Estudio") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(price, { price = it }, label = { Text("Precio (S/)") }, singleLine = true)
                OutlinedTextField(quantity, { quantity = it }, label = { Text("Cantidad") }, singleLine = true)
                Text("Categoría", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories.drop(1)) { option ->
                        FilterChip(selected = category == option, onClick = { category = option }, label = { Text(option) })
                    }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = { Button(onClick = { error = onSave(name, price, quantity, category) }) { Text("Agregar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
