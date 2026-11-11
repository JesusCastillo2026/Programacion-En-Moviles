package com.tecsup.cinecastillo

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
import com.tecsup.cinecastillo.data.auth.Role
import com.tecsup.cinecastillo.data.auth.SessionState
import com.tecsup.cinecastillo.ui.AdminScreen
import com.tecsup.cinecastillo.ui.CatalogScreen
import com.tecsup.cinecastillo.ui.CineTheme
import com.tecsup.cinecastillo.ui.CreditsScreen
import com.tecsup.cinecastillo.ui.DetailScreen
import com.tecsup.cinecastillo.ui.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SyncNotifications.createChannel(this)
        val container = AppContainer(this)
        setContent { CineTheme { CineApp(container) } }
    }
}

@Composable
private fun CineApp(container: AppContainer) {
    if (!BuildConfig.FIREBASE_CONFIGURED) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Falta app/google-services.json. Registra esta app en Firebase para iniciar sesión.")
        }
        return
    }
    val auth: AuthViewModel = viewModel(factory = AuthViewModel.Factory(container.auth))
    val session by auth.session.collectAsStateWithLifecycle()
    val busy by auth.busy.collectAsStateWithLifecycle()
    val error by auth.error.collectAsStateWithLifecycle()
    when (val current = session) {
        SessionState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        SessionState.SignedOut -> LoginScreen(busy, error, auth::login)
        is SessionState.Error -> LoginScreen(busy, current.message, auth::login)
        is SessionState.SignedIn -> SignedInApp(container, current.session.role, current.session.email, auth::signOut)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignedInApp(container: AppContainer, role: Role, email: String, onSignOut: () -> Unit) {
    val listVm: MovieListViewModel = viewModel(factory = MovieListViewModel.Factory(container))
    val state by listVm.uiState.collectAsStateWithLifecycle()
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
    LaunchedEffect(route) { listVm.showFavorites(route == "favorites") }

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
                CatalogScreen(state, listVm::search, listVm::sort, listVm::showFavorites, listVm::sync, { nav.navigate("detail/$it") }, listVm::setFavorite)
            }
            composable("favorites") {
                CatalogScreen(state, listVm::search, listVm::sort, listVm::showFavorites, listVm::sync, { nav.navigate("detail/$it") }, listVm::setFavorite)
            }
            composable("admin") {
                if (role == Role.ADMIN) AdminScreen(state, listVm::sync, listVm::clearCache)
                else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Acceso restringido") }
            }
            composable("credits") { CreditsScreen() }
            composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: return@composable
                val detail: MovieDetailViewModel = viewModel(key = "detail_$id", factory = MovieDetailViewModel.Factory(id, container.movies))
                val detailState by detail.uiState.collectAsStateWithLifecycle()
                DetailScreen(detailState, onBack = { nav.popBackStack() }, onRefresh = detail::refresh, onFavorite = detail::setFavorite)
            }
        }
    }
}

