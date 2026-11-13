package com.tecsup.cinecastillo.ui.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.BuildConfig
import com.tecsup.cinecastillo.R
import com.tecsup.cinecastillo.domain.model.Role
import com.tecsup.cinecastillo.domain.model.SessionState
import com.tecsup.cinecastillo.ui.CreditsScreen
import com.tecsup.cinecastillo.ui.admin.AdminScreen
import com.tecsup.cinecastillo.ui.admin.AdminViewModel
import com.tecsup.cinecastillo.ui.components.Cargando
import com.tecsup.cinecastillo.ui.components.ErrorView
import com.tecsup.cinecastillo.ui.components.EstadoContenido
import com.tecsup.cinecastillo.ui.detalle.DetailScreen
import com.tecsup.cinecastillo.ui.detalle.MovieDetailViewModel
import com.tecsup.cinecastillo.ui.favoritos.FavoritosScreen
import com.tecsup.cinecastillo.ui.favoritos.FavoritosViewModel
import com.tecsup.cinecastillo.ui.lista.CatalogScreen
import com.tecsup.cinecastillo.ui.lista.MovieListViewModel
import com.tecsup.cinecastillo.ui.login.AuthViewModel
import com.tecsup.cinecastillo.ui.login.LoginScreen
import com.tecsup.cinecastillo.ui.login.RegisterScreen
import com.tecsup.cinecastillo.ui.profile.ProfileScreen
import com.tecsup.cinecastillo.ui.profile.ProfileViewModel
import com.tecsup.cinecastillo.ui.theme.CineTheme
import kotlinx.coroutines.launch

@Composable
fun CineApp(container: AppContainer) {
    if (!BuildConfig.FIREBASE_CONFIGURED) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Falta app/google-services.json. Registra esta app en Firebase para iniciar sesión.")
        }
        return
    }
    val auth: AuthViewModel = viewModel(factory = AuthViewModel.Factory(container))
    val session by auth.estado.collectAsStateWithLifecycle()
    val busy by auth.busy.collectAsStateWithLifecycle()
    val error by auth.error.collectAsStateWithLifecycle()
    val registrationFeedback by auth.registrationFeedback.collectAsStateWithLifecycle()
    val passwordResetBusy by auth.passwordResetBusy.collectAsStateWithLifecycle()
    val passwordResetFeedback by auth.passwordResetFeedback.collectAsStateWithLifecycle()
    val passwordResetSuccess by auth.passwordResetSuccess.collectAsStateWithLifecycle()
    var registerMode by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(registrationFeedback) { if (registrationFeedback != null) registerMode = false }

    when (val current = session) {
        com.tecsup.cinecastillo.ui.UiState.Cargando -> Cargando()
        is com.tecsup.cinecastillo.ui.UiState.Error -> ErrorView(current.mensaje, auth::reintentarSesion)
        is com.tecsup.cinecastillo.ui.UiState.Exito -> when (val value = current.datos) {
            is SessionState.SignedIn -> androidx.compose.runtime.key(value.session.uid) {
                SignedInApp(container, value.session.role, value.session.email, auth::signOut)
            }
            else -> if (registerMode) {
                RegisterScreen(busy, error, auth::register) { auth.clearError(); registerMode = false }
            } else {
                LoginScreen(
                    busy = busy,
                    error = error,
                    notice = registrationFeedback,
                    passwordResetBusy = passwordResetBusy,
                    passwordResetFeedback = passwordResetFeedback,
                    passwordResetSuccess = passwordResetSuccess,
                    onLogin = auth::login,
                    onOpenRegister = { auth.clearError(); registerMode = true },
                    onRequestPasswordReset = auth::enviarRestablecimiento,
                    onClearPasswordResetFeedback = auth::limpiarFeedbackRestablecimiento
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignedInApp(container: AppContainer, role: Role, email: String, onSignOut: () -> Unit) {
    val listVm: MovieListViewModel = viewModel(factory = MovieListViewModel.Factory(container))
    val state by listVm.estado.collectAsStateWithLifecycle()
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val routeName = route?.substringBefore("?")
    val context = androidx.compose.ui.platform.LocalContext.current
    val drawerState = androidx.compose.material3.rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var notificationsAllowed by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notificationsAllowed = granted
        container.notificador.permisoActualizado(granted)
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationsAllowed) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.72f),
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Image(painterResource(R.drawable.cine_castillo_logo), "Cine Castillo", Modifier.size(88.dp))
                    Text("CINE CASTILLO", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDrawerItem("Quiénes somos", "about", route) { nav.navigate("about"); scope.launch { drawerState.close() } }
                HorizontalDrawerItem("Formatos de sala", "formats", route) { nav.navigate("formats"); scope.launch { drawerState.close() } }
                HorizontalDrawerItem("Créditos", "credits", route) { nav.navigate("credits"); scope.launch { drawerState.close() } }
                HorizontalDrawerItem("Mi perfil", "profile", route) { nav.navigate("profile"); scope.launch { drawerState.close() } }
                Spacer(Modifier.height(10.dp))
                Text("Una experiencia de cine para cada historia.", Modifier.padding(horizontal = 22.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.blur(if (drawerState.isOpen) 12.dp else 0.dp),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Image(painterResource(R.drawable.cine_castillo_logo), "Logo Cine Castillo", Modifier.size(34.dp), contentScale = ContentScale.Fit)
                            Text("Cine Castillo")
                        }
                    },
                    navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (routeName != "catalog") {
                                IconButton(onClick = {
                                    if (!nav.popBackStack()) nav.navigate("catalog") { launchSingleTop = true }
                                }) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                            }
                        }
                    },
                    actions = {
                        if (routeName != "catalog") {
                            IconButton(onClick = {
                                nav.navigate("catalog") {
                                    popUpTo("catalog") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }) { Icon(Icons.Default.Home, contentDescription = "Ir al inicio") }
                        }
                        TextButton(onClick = onSignOut) { Text("Salir") }
                    }
                )
            },
            bottomBar = {
                if (routeName in listOf("catalog", "booking", "favorites", "admin", "profile")) NavigationBar {
                    NavigationBarItem(selected = routeName == "catalog", onClick = { nav.navigate("catalog") { launchSingleTop = true } }, icon = { Text("🎬") }, label = { Text("Catálogo") })
                    NavigationBarItem(selected = routeName == "booking", onClick = { nav.navigate("booking") { launchSingleTop = true } }, icon = { Text("🎟") }, label = { Text("Entradas") })
                    NavigationBarItem(selected = routeName == "favorites", onClick = { nav.navigate("favorites") { launchSingleTop = true } }, icon = { Text("♥") }, label = { Text("Favoritos") })
                    if (role == Role.ADMIN) NavigationBarItem(selected = routeName == "admin", onClick = { nav.navigate("admin") { launchSingleTop = true } }, icon = { Text("⚙") }, label = { Text("Admin") })
                    NavigationBarItem(selected = routeName == "profile", onClick = { nav.navigate("profile") { launchSingleTop = true } }, icon = { Text("●") }, label = { Text("Perfil") })
                }
            }
        ) { padding ->
            NavHost(navController = nav, startDestination = "catalog", modifier = Modifier.padding(padding)) {
                composable("catalog") {
                    EstadoContenido(state, listVm::sync) { data ->
                        CatalogScreen(data, listVm::search, listVm::sort, listVm::showFavorites, listVm::sync, { nav.navigate("detail/$it") }, listVm::setFavorite, onShowtimes = { nav.navigate("shows") })
                    }
                }
                composable("favorites") {
                    val vm: FavoritosViewModel = viewModel(factory = FavoritosViewModel.Factory(container))
                    val result by vm.estado.collectAsStateWithLifecycle()
                    EstadoContenido(result, {}) { data -> FavoritosScreen(data, { nav.navigate("detail/$it") }, vm::alternar) }
                }
                composable("shows") {
                    val catalog by listVm.uiState.collectAsStateWithLifecycle()
                    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
                        com.tecsup.cinecastillo.ui.booking.CarteleraScreen(
                            movies = catalog.movies,
                            onReserve = { movie, venue, offset, time ->
                                nav.navigate("booking?movieId=${movie.id}&venue=${android.net.Uri.encode(venue.name)}&dayOffset=$offset&time=${android.net.Uri.encode(time)}")
                            },
                            onMovieDetails = { nav.navigate("detail/$it") },
                            loading = catalog.loading,
                            error = catalog.error,
                            offline = catalog.offline,
                            onRetry = listVm::sync
                        )
                    }
                }
                composable(
                    "booking?movieId={movieId}&venue={venue}&dayOffset={dayOffset}&time={time}",
                    arguments = listOf(
                        navArgument("movieId") { type = NavType.IntType; defaultValue = -1 },
                        navArgument("venue") { type = NavType.StringType; defaultValue = "" },
                        navArgument("dayOffset") { type = NavType.IntType; defaultValue = 0 },
                        navArgument("time") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStack ->
                    val catalog by listVm.uiState.collectAsStateWithLifecycle()
                    val bookingVm: com.tecsup.cinecastillo.ui.booking.BookingViewModel = viewModel(factory = com.tecsup.cinecastillo.ui.booking.BookingViewModel.Factory(container))
                    val bookingState by bookingVm.state.collectAsStateWithLifecycle()
                    val movieId = backStack.arguments?.getInt("movieId")?.takeIf { it > 0 }
                    val venue = backStack.arguments?.getString("venue").orEmpty()
                    val dayOffset = backStack.arguments?.getInt("dayOffset") ?: 0
                    val time = backStack.arguments?.getString("time").orEmpty()
                    com.tecsup.cinecastillo.ui.booking.BookingScreen(
                        state = catalog,
                        initialMovieId = movieId,
                        initialVenue = venue,
                        initialDayOffset = dayOffset,
                        initialTime = time,
                        bookingState = bookingState,
                        onConfirmReservation = bookingVm::confirm,
                        onResetReservation = bookingVm::clearResult,
                        onRetryCatalog = listVm::sync
                    )
                }
                composable("profile") {
                    val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(container))
                    val profile by vm.state.collectAsStateWithLifecycle()
                    LaunchedEffect(Unit) { vm.load() }
                    ProfileScreen(profile, vm::updateContact, vm::clearMessage)
                }
                composable("about") { AboutCineCastillo() }
                composable("formats") { FormatInfoScreen() }
                composable("credits") { CreditsScreen() }
                composable("admin") {
                    if (role == Role.ADMIN) {
                        val vm: AdminViewModel = viewModel(factory = AdminViewModel.Factory(container))
                        val result by vm.estado.collectAsStateWithLifecycle()
                        EstadoContenido(result, vm::sincronizar) { data -> AdminScreen(data, vm::sincronizar, vm::limpiar) }
                    } else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Acceso restringido") }
                }
                composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStack ->
                    val id = backStack.arguments?.getInt("id") ?: return@composable
                    val detail: MovieDetailViewModel = viewModel(key = "detail_$id", factory = MovieDetailViewModel.Factory(id, container))
                    val detailState by detail.estado.collectAsStateWithLifecycle()
                    EstadoContenido(detailState, detail::refresh) { data -> DetailScreen(data, onRefresh = detail::refresh, onFavorite = detail::setFavorite) }
                }
            }
        }
    }
}

@Composable
private fun HorizontalDrawerItem(label: String, destination: String, currentRoute: String?, onClick: () -> Unit) {
    NavigationDrawerItem(label = { Text(label) }, selected = currentRoute == destination, onClick = onClick, modifier = Modifier.padding(horizontal = 12.dp))
}

@Composable
private fun AboutCineCastillo() {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(R.drawable.cine_castillo_logo), "Logo Cine Castillo", Modifier.size(150.dp))
        Text("Quiénes somos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Cine Castillo es un proyecto académico que reúne cartelera, favoritos y una experiencia de reserva de demostración en una sola aplicación.", style = MaterialTheme.typography.bodyLarge)
        Text("Las funciones, sedes y disponibilidad se muestran con datos de muestra; TMDB provee los datos de películas.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun FormatInfoScreen() {
    val formats = listOf(
        Triple("2D", R.drawable.format_2d, "La experiencia clásica con imagen digital y sonido envolvente."),
        Triple("3D", R.drawable.format_3d, "Una proyección con sensación de profundidad."),
        Triple("IMAX", R.drawable.format_imax, "Pantalla de gran formato para una experiencia inmersiva."),
        Triple("4DX", R.drawable.format_4dx, "Asientos en movimiento y efectos ambientales simulados.")
    )
    androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Formatos de sala", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        items(formats.size) { index ->
            val (name, image, description) = formats[index]
            androidx.compose.material3.Card(shape = RoundedCornerShape(22.dp), colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                Column {
                    Image(painterResource(image), "Experiencia $name", Modifier.fillMaxWidth().height(180.dp), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        item { Text("Imágenes y descripciones ilustrativas. Los formatos dependen de la película y la sede.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}
