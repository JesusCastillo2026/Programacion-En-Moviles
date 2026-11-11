package com.tecsup.cinecastillo.ui.navigation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.tecsup.cinecastillo.domain.model.Role
import com.tecsup.cinecastillo.domain.model.SessionState
import com.tecsup.cinecastillo.ui.admin.AdminScreen
import com.tecsup.cinecastillo.ui.lista.CatalogScreen
import com.tecsup.cinecastillo.ui.theme.CineTheme
import com.tecsup.cinecastillo.ui.CreditsScreen
import com.tecsup.cinecastillo.ui.detalle.DetailScreen
import com.tecsup.cinecastillo.ui.login.LoginScreen

import com.tecsup.cinecastillo.ui.UiState
import com.tecsup.cinecastillo.ui.components.*
import com.tecsup.cinecastillo.ui.favoritos.*
import com.tecsup.cinecastillo.ui.admin.AdminViewModel
import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.BuildConfig
import com.tecsup.cinecastillo.ui.login.AuthViewModel
import com.tecsup.cinecastillo.ui.lista.MovieListViewModel
import com.tecsup.cinecastillo.ui.detalle.MovieDetailViewModel

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
    when (val current = session) {
        UiState.Cargando -> Cargando()
        is UiState.Error -> ErrorView(current.mensaje, auth::reintentarSesion)
        is UiState.Exito -> when (val value = current.datos) {
            is SessionState.SignedIn -> androidx.compose.runtime.key(value.session.uid) {
                SignedInApp(container, value.session.role, value.session.email, auth::signOut)
            }
            else -> LoginScreen(busy, error, auth::login)
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
    val context = androidx.compose.ui.platform.LocalContext.current
    var notificationsAllowed by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notificationsAllowed = granted
        container.notificador.permisoActualizado(granted)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationsAllowed) {
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cine Castillo") },
                actions = { TextButton(onClick = onSignOut) { Text("Salir") } }
            )
        },
        bottomBar = {
            if (route != "detail/{id}") NavigationBar {
                NavigationBarItem(selected = route == "catalog", onClick = { nav.navigate("catalog") { launchSingleTop = true; popUpTo("catalog") } }, icon = { Text("🎬") }, label = { Text("Catálogo") })
                NavigationBarItem(selected = route == "favorites", onClick = { nav.navigate("favorites") { launchSingleTop = true } }, icon = { Text("♥") }, label = { Text("Favoritos") })
                if (role == Role.ADMIN) NavigationBarItem(selected = route == "admin", onClick = { nav.navigate("admin") { launchSingleTop = true } }, icon = { Text("⚙") }, label = { Text("Admin") })
                NavigationBarItem(selected = route == "credits", onClick = { nav.navigate("credits") { launchSingleTop = true } }, icon = { Text("ⓘ") }, label = { Text("Créditos") })
            }
        }
    ) { padding ->
        NavHost(navController = nav, startDestination = "catalog", modifier = Modifier.padding(padding)) {
            composable("catalog") {
                EstadoContenido(state, listVm::sync) { data ->
                    CatalogScreen(data, listVm::search, listVm::sort, listVm::showFavorites, listVm::sync, { nav.navigate("detail/$it") }, listVm::setFavorite)
                }
            }
            composable("favorites") {
                val vm: FavoritosViewModel = viewModel(factory = FavoritosViewModel.Factory(container))
                val result by vm.estado.collectAsStateWithLifecycle()
                EstadoContenido(result, {}) { data -> FavoritosScreen(data, { nav.navigate("detail/$it") }, vm::alternar) }
            }
            composable("admin") {
                if (role == Role.ADMIN) {
                    val vm: AdminViewModel = viewModel(factory = AdminViewModel.Factory(container))
                    val result by vm.estado.collectAsStateWithLifecycle()
                    EstadoContenido(result, vm::sincronizar) { data -> AdminScreen(data, vm::sincronizar, vm::limpiar) }
                }
                else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Acceso restringido") }
            }
            composable("credits") { CreditsScreen() }
            composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: return@composable
                val detail: MovieDetailViewModel = viewModel(key = "detail_$id", factory = MovieDetailViewModel.Factory(id, container))
                val detailState by detail.estado.collectAsStateWithLifecycle()
                EstadoContenido(detailState, detail::refresh) { data ->
                    DetailScreen(data, onBack = { nav.popBackStack() }, onRefresh = detail::refresh, onFavorite = detail::setFavorite)
                }
            }
        }
    }
}

