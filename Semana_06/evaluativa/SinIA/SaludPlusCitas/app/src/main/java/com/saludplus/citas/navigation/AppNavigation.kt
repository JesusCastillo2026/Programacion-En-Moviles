package com.saludplus.citas.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saludplus.citas.ui.screens.agendamiento.CitaExitosaScreen
import com.saludplus.citas.ui.screens.agendamiento.ConfirmarCitaScreen
import com.saludplus.citas.ui.screens.agendamiento.EspecialidadesScreen
import com.saludplus.citas.ui.screens.agendamiento.FechaHoraScreen
import com.saludplus.citas.ui.screens.agendamiento.MedicosScreen
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen
import com.saludplus.citas.ui.screens.auth.TerminosScreen
import com.saludplus.citas.ui.screens.citas.DetalleCitaScreen
import com.saludplus.citas.ui.screens.citas.MisCitasScreen
import com.saludplus.citas.ui.screens.home.HomeScreen
import com.saludplus.citas.ui.screens.notificaciones.NotificacionesScreen
import com.saludplus.citas.ui.screens.perfil.PerfilScreen
import com.saludplus.citas.ui.screens.resultados.ResultadosScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: Rutas.SPLASH
    val tabs = listOf(Rutas.INICIO to "Inicio", Rutas.CITAS to "Citas",
        Rutas.RESULTADOS to "Resultados", Rutas.PERFIL to "Perfil")
    val mainTab = tabs.any { it.first == route }
    val auth = route in listOf(Rutas.SPLASH, Rutas.REGISTRO, Rutas.LOGIN, Rutas.TERMINOS)
    val title = when (route) {
        Rutas.INICIO -> "Clínica SaludPlus"
        Rutas.CITAS -> "Mis citas"
        Rutas.RESULTADOS -> "Resultados"
        Rutas.PERFIL -> "Mi perfil"
        Rutas.ESPECIALIDADES -> "Especialidades"
        Rutas.MEDICOS -> "Médicos"
        Rutas.FECHA_HORA -> "Fecha y hora"
        Rutas.CONFIRMAR -> "Confirmar cita"
        Rutas.EXITOSA -> "Cita agendada"
        Rutas.DETALLE -> "Detalle de cita"
        Rutas.NOTIFICACIONES -> "Notificaciones"
        else -> ""
    }
    fun goTab(destination: String) {
        nav.navigate(destination) {
            popUpTo(Rutas.INICIO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    Scaffold(
        topBar = {
            if (!auth) TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (!mainTab) IconButton(onClick = { nav.popBackStack() }) { Text("←") }
                }
            )
        },
        bottomBar = {
            if (mainTab) NavigationBar {
                tabs.forEach { (destination, label) ->
                    NavigationBarItem(
                        selected = route == destination,
                        onClick = { goTab(destination) },
                        icon = { Text(if (route == destination) "●" else "○") },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = Rutas.SPLASH, modifier = Modifier.padding(padding)) {
            composable(Rutas.SPLASH) {
                SplashScreen(onRegistro = { nav.navigate(Rutas.REGISTRO) },
                    onLogin = { nav.navigate(Rutas.LOGIN) })
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(onSuccess = {
                    nav.navigate(Rutas.INICIO) { popUpTo(Rutas.SPLASH) { inclusive = true } }
                }, onLogin = { nav.navigate(Rutas.LOGIN) },
                    onTerminos = { nav.navigate(Rutas.TERMINOS) })
            }
            composable(Rutas.LOGIN) {
                LoginScreen(onSuccess = {
                    nav.navigate(Rutas.INICIO) { popUpTo(Rutas.SPLASH) { inclusive = true } }
                }, onRegistro = { nav.navigate(Rutas.REGISTRO) })
            }
            composable(Rutas.INICIO) {
                HomeScreen(onEspecialidades = { nav.navigate(Rutas.ESPECIALIDADES) },
                    onEspecialidad = { nav.navigate(Rutas.medicos(it)) },
                    onCitas = { goTab(Rutas.CITAS) },
                    onNotificaciones = { nav.navigate(Rutas.NOTIFICACIONES) })
            }
            composable(Rutas.ESPECIALIDADES) {
                EspecialidadesScreen(onElegir = { nav.navigate(Rutas.medicos(it)) })
            }
            composable(Rutas.MEDICOS, arguments = listOf(navArgument("especialidadId") {
                type = NavType.IntType
            })) {
                MedicosScreen(it.arguments?.getInt("especialidadId") ?: -1,
                    onElegir = { medicoId -> nav.navigate(Rutas.fechaHora(medicoId)) })
            }
            composable(Rutas.FECHA_HORA, arguments = listOf(navArgument("medicoId") {
                type = NavType.IntType
            })) {
                val medicoId = it.arguments?.getInt("medicoId") ?: -1
                FechaHoraScreen(medicoId, onContinuar = { fecha, hora ->
                    nav.navigate(Rutas.confirmar(medicoId, fecha, hora))
                })
            }
            composable(Rutas.CONFIRMAR, arguments = listOf(
                navArgument("medicoId") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType }
            )) {
                ConfirmarCitaScreen(
                    medicoId = it.arguments?.getInt("medicoId") ?: -1,
                    fecha = it.arguments?.getString("fecha").orEmpty(),
                    hora = it.arguments?.getString("hora").orEmpty(),
                    onConfirmar = { citaId ->
                        nav.navigate(Rutas.exitosa(citaId)) {
                            popUpTo(Rutas.INICIO)
                        }
                    }
                )
            }
            composable(Rutas.EXITOSA, arguments = listOf(navArgument("citaId") {
                type = NavType.IntType
            })) {
                CitaExitosaScreen(it.arguments?.getInt("citaId") ?: -1,
                    onCitas = { goTab(Rutas.CITAS) },
                    onInicio = { goTab(Rutas.INICIO) })
            }
            composable(Rutas.CITAS) {
                MisCitasScreen(onDetalle = { nav.navigate(Rutas.detalle(it)) })
            }
            composable(Rutas.DETALLE, arguments = listOf(navArgument("citaId") {
                type = NavType.IntType
            })) {
                DetalleCitaScreen(it.arguments?.getInt("citaId") ?: -1,
                    onVolver = { nav.popBackStack() })
            }
            composable(Rutas.PERFIL) {
                PerfilScreen(onCerrarSesion = {
                    nav.navigate(Rutas.SPLASH) { popUpTo(Rutas.INICIO) { inclusive = true } }
                }, onTerminos = { nav.navigate(Rutas.TERMINOS) })
            }
            composable(Rutas.RESULTADOS) { ResultadosScreen() }
            composable(Rutas.NOTIFICACIONES) {
                NotificacionesScreen(onDetalle = { nav.navigate(Rutas.detalle(it)) })
            }
            composable(Rutas.TERMINOS) { TerminosScreen(onVolver = { nav.popBackStack() }) }
        }
    }
}
