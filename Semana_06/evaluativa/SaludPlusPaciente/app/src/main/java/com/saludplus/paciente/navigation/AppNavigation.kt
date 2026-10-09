package com.saludplus.paciente.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.screens.auth.LoginScreen
import com.saludplus.paciente.ui.screens.auth.RegistroScreen
import com.saludplus.paciente.ui.screens.auth.SplashScreen
import com.saludplus.paciente.ui.screens.auth.TerminosScreen
import com.saludplus.paciente.ui.screens.home.HomeScreen

private data class TabItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    TabItem(Rutas.INICIO, "Inicio", Icons.Default.Home),
    TabItem(Rutas.MIS_CITAS, "Citas", Icons.Default.CalendarMonth),
    TabItem(Rutas.RESULTADOS, "Resultados", Icons.Default.Assignment),
    TabItem(Rutas.PERFIL, "Perfil", Icons.Default.Person)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val showNavigationBar = route in tabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showNavigationBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = backStack?.destination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = { navController.openTab(tab.route) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(Rutas.SPLASH) {
                SplashScreen(
                    onRegister = { navController.navigate(Rutas.REGISTRO) },
                    onLogin = { navController.navigate(Rutas.LOGIN) }
                )
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    onBack = { navController.popBackStack() },
                    onRegister = { nombre, correo, telefono, contrasena ->
                        val usuario = Repositorio.registrarUsuario(nombre, correo, telefono, contrasena)
                        if (usuario != null) navController.irAInicio()
                        usuario != null
                    },
                    onLogin = { navController.navigate(Rutas.LOGIN) },
                    onTerms = { navController.navigate(Rutas.TERMINOS) }
                )
            }
            composable(Rutas.LOGIN) {
                LoginScreen(
                    onBack = { navController.popBackStack() },
                    onLogin = { correo, contrasena ->
                        val usuario = Repositorio.iniciarSesion(correo, contrasena)
                        if (usuario != null) navController.irAInicio()
                        usuario != null
                    },
                    onRegister = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.TERMINOS) { TerminosScreen(onBack = { navController.popBackStack() }) }
            composable(Rutas.INICIO) {
                HomeScreen(
                    onExplore = { navController.navigate(Rutas.ESPECIALIDADES) },
                    onNotifications = { navController.navigate(Rutas.NOTIFICACIONES) }
                )
            }
            composable(Rutas.ESPECIALIDADES) { TabPlaceholder("Especialidades", onBack = { navController.popBackStack() }) }
            composable(Rutas.MIS_CITAS) { TabPlaceholder("Mis citas") }
            composable(Rutas.RESULTADOS) { TabPlaceholder("Resultados") }
            composable(Rutas.PERFIL) { TabPlaceholder("Perfil") }
            composable(Rutas.NOTIFICACIONES) { TabPlaceholder("Notificaciones", onBack = { navController.popBackStack() }) }
        }
    }
}

private fun NavHostController.irAInicio() {
    navigate(Rutas.INICIO) {
        popUpTo(Rutas.SPLASH) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.openTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun TabPlaceholder(title: String, onBack: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        if (onBack != null) androidx.compose.material3.TextButton(onClick = onBack) { Text("Volver") }
    }
}

