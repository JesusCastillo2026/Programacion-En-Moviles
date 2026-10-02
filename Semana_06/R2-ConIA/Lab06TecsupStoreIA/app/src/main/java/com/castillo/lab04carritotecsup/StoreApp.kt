package com.castillo.lab04carritotecsup

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val StoreColors = lightColorScheme(
    primary = Color(0xFF185C55),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3F1E8),
    secondary = Color(0xFF395A96),
    background = Color(0xFFF5F8F7),
    surface = Color.White,
    surfaceVariant = Color(0xFFEAF1EF)
)

/** Rutas pequeñas y estables para las cuatro secciones pedidas por el laboratorio. */
enum class StoreRoute(val titulo: String) {
    INICIO("Inicio"), PEDIDOS("Mis pedidos"), FAVORITOS("Favoritos"), PERFIL("Perfil")
}

/**
 * El estado se crea una sola vez encima del drawer y las pantallas. Esta elevación de estado es
 * la conexión entre el menú contextual de una tarjeta y el badge de Favoritos del menú lateral.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreApp() {
    val state = remember { StoreState() }
    var routeName by rememberSaveable { mutableStateOf(StoreRoute.INICIO.name) }
    val route = StoreRoute.valueOf(routeName)
    val drawerState = androidx.compose.material3.rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var reportProduct by remember { mutableStateOf<Producto?>(null) }

    fun feedback(message: String) { scope.launch { snackbar.showSnackbar(message) } }
    fun share(producto: Producto) {
        val message = "Mira ${producto.nombre} en TECSUP Store: ${money(producto.precio)}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        runCatching { context.startActivity(Intent.createChooser(intent, "Compartir producto")) }
            .onFailure { feedback("No hay una aplicación disponible para compartir.") }
    }

    MaterialTheme(colorScheme = StoreColors) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(route, state.totalFavoritos) { destination ->
                    routeName = destination.name
                    scope.launch { drawerState.close() }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(if (route == StoreRoute.INICIO) "TECSUP Store" else route.titulo) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Abrir menú de navegación")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = StoreColors.primary,
                            titleContentColor = StoreColors.onPrimary,
                            navigationIconContentColor = StoreColors.onPrimary
                        )
                    )
                },
                snackbarHost = { SnackbarHost(snackbar) },
                floatingActionButton = {
                    if (route == StoreRoute.INICIO) {
                        FloatingActionButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                        }
                    }
                }
            ) { padding ->
                when (route) {
                    StoreRoute.INICIO -> CatalogScreen(
                        state = state,
                        padding = padding,
                        onFavorite = { producto ->
                            state.alternarFavorito(producto.id)
                            feedback(if (state.esFavorito(producto.id)) "Agregado a Favoritos" else "Quitado de Favoritos")
                        },
                        onShare = ::share,
                        onReport = { reportProduct = it },
                        onOrder = { producto ->
                            feedback(if (state.crearPedido(producto.id)) "Pedido demo registrado" else "Sin unidades disponibles")
                        }
                    )
                    StoreRoute.PEDIDOS -> OrdersScreen(state, padding)
                    StoreRoute.FAVORITOS -> FavoritesScreen(
                        state, padding,
                        onFavorite = { state.alternarFavorito(it.id) },
                        onShare = ::share,
                        onReport = { reportProduct = it },
                        onOrder = { producto ->
                            feedback(if (state.crearPedido(producto.id)) "Pedido demo registrado" else "Sin unidades disponibles")
                        }
                    )
                    StoreRoute.PERFIL -> ProfileScreen(state, padding)
                }
            }
        }
        if (showAddDialog) {
            AddProductDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, price, quantity, category ->
                    val error = state.agregarProducto(name, price, quantity, category)
                    if (error == null) {
                        showAddDialog = false
                        feedback("Producto agregado al catálogo")
                    }
                    error
                }
            )
        }
        reportProduct?.let { product ->
            ReportProductDialog(
                producto = product,
                onDismiss = { reportProduct = null },
                onConfirm = {
                    state.reportar(product.id)
                    reportProduct = null
                    feedback("Reporte demo guardado en esta sesión")
                }
            )
        }
    }
}

/** Drawer de alcance global. El badge se deriva de favoritos; no tiene contador duplicado. */
@Composable
fun AppDrawer(route: StoreRoute, favoriteCount: Int, onNavigate: (StoreRoute) -> Unit) {
    ModalDrawerSheet {
        Column(Modifier.fillMaxWidth().padding(22.dp)) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Box(Modifier.size(54.dp), contentAlignment = Alignment.Center) {
                    Text("JC", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Jesús Castillo Sumire", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Comunidad TECSUP", style = MaterialTheme.typography.bodySmall)
        }
        HorizontalDivider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        StoreRoute.entries.forEach { destination ->
            val icon = when (destination) {
                StoreRoute.INICIO -> Icons.Default.Home
                StoreRoute.PEDIDOS -> Icons.Default.ShoppingCart
                StoreRoute.FAVORITOS -> Icons.Default.Favorite
                StoreRoute.PERFIL -> Icons.Default.Person
            }
            NavigationDrawerItem(
                label = { Text(destination.titulo) },
                icon = { Icon(icon, contentDescription = null) },
                badge = {
                    if (destination == StoreRoute.FAVORITOS && favoriteCount > 0) {
                        Badge { Text(favoriteCount.toString()) }
                    }
                },
                selected = route == destination,
                onClick = { onNavigate(destination) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Demostración local · Semana 06",
            modifier = Modifier.padding(horizontal = 28.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
