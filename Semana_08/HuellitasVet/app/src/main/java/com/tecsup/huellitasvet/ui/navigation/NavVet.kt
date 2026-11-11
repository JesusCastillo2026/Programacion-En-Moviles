package com.tecsup.huellitasvet.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tecsup.huellitasvet.VetApp
import com.tecsup.huellitasvet.ui.dueno.LoginScreen
import com.tecsup.huellitasvet.ui.dueno.InicioScreen
import com.tecsup.huellitasvet.ui.dueno.MascotasScreen
import com.tecsup.huellitasvet.ui.dueno.FormularioMascotaScreen
import com.tecsup.huellitasvet.ui.dueno.FichaMascotaScreen
import com.tecsup.huellitasvet.ui.dueno.PedirCitaScreen
import com.tecsup.huellitasvet.ui.dueno.MisCitasScreen
import com.tecsup.huellitasvet.ui.dueno.SplashScreen
import com.tecsup.huellitasvet.viewmodel.InicioViewModel
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel
import com.tecsup.huellitasvet.viewmodel.PedirCitaViewModel
import com.tecsup.huellitasvet.viewmodel.MisCitasViewModel
import com.tecsup.huellitasvet.viewmodel.PanelViewModel
import com.tecsup.huellitasvet.viewmodel.AgendaViewModel
import com.tecsup.huellitasvet.ui.veterinario.PanelScreen
import com.tecsup.huellitasvet.ui.veterinario.AgendaScreen
import com.tecsup.huellitasvet.ui.veterinario.AtenderCitaScreen
import com.tecsup.huellitasvet.ui.veterinario.PacientesScreen
import com.tecsup.huellitasvet.viewmodel.AtenderCitaViewModel
import com.tecsup.huellitasvet.viewmodel.PacientesViewModel
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel
import com.tecsup.huellitasvet.viewmodel.PerfilViewModel
import com.tecsup.huellitasvet.ui.veterinario.ServiciosScreen
import com.tecsup.huellitasvet.ui.veterinario.FormularioServicioScreen
import com.tecsup.huellitasvet.ui.veterinario.AjustesScreen
import com.tecsup.huellitasvet.ui.dueno.PerfilScreen
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.viewmodel.SesionViewModel
import com.tecsup.huellitasvet.viewmodel.VetViewModelFactory

@Composable
fun NavVet(sesion: SesionViewModel, app: VetApp) {
    val nav = rememberNavController()
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val cargando by sesion.cargando.collectAsStateWithLifecycle()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val dueno = listOf(Rutas.INICIO to "Inicio", Rutas.MASCOTAS to "Mascotas", Rutas.CITAS to "Citas", Rutas.PERFIL to "Perfil")
    val veterinario = listOf(Rutas.PANEL to "Panel", Rutas.AGENDA to "Agenda", Rutas.PACIENTES to "Pacientes", Rutas.SERVICIOS to "Servicios", Rutas.AJUSTES to "Ajustes")
    val destinos = if (usuario?.rol == "ADMIN") veterinario else dueno

    LaunchedEffect(cargando, usuario?.id, usuario?.rol) {
        if (!cargando) {
            nav.navigate(if (usuario == null) Rutas.LOGIN else if (usuario?.rol == "ADMIN") Rutas.PANEL else Rutas.INICIO) {
                popUpTo(nav.graph.id) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    Scaffold(topBar = {
        val titulo = when (ruta) {
            Rutas.FICHA -> "Ficha de mascota"
            Rutas.FORM_MASCOTA -> "Mascota"
            Rutas.PEDIR_CITA -> "Pedir cita"
            Rutas.ATENDER -> "Atender cita"
            Rutas.FORM_SERVICIO -> "Servicio"
            else -> null
        }
        if (titulo != null) AppTopBar(titulo) { nav.popBackStack() }
    }, bottomBar = {
        if (ruta in destinos.map { it.first }) NavigationBar {
            destinos.forEach { (destino, titulo) ->
                NavigationBarItem(
                    selected = ruta == destino,
                    onClick = { nav.navigate(destino) { popUpTo(destinos.first().first); launchSingleTop = true; restoreState = true } },
                    icon = {
                        val icono = when (destino) {
                            Rutas.INICIO -> Icons.Default.Home
                            Rutas.MASCOTAS, Rutas.PACIENTES -> Icons.Default.Pets
                            Rutas.CITAS -> Icons.Default.CalendarMonth
                            Rutas.PERFIL -> Icons.Default.Person
                            Rutas.PANEL -> Icons.Default.Dashboard
                            Rutas.AGENDA -> Icons.Default.Event
                            Rutas.SERVICIOS -> Icons.Default.MedicalServices
                            else -> Icons.Default.Settings
                        }
                        Icon(icono, contentDescription = titulo)
                    },
                    label = { Text(titulo) }
                )
            }
        }
    }) { margen ->
        NavHost(navController = nav, startDestination = Rutas.SPLASH, modifier = Modifier.padding(margen)) {
            composable(Rutas.SPLASH) { SplashScreen() }
            composable(Rutas.LOGIN) { LoginScreen(sesion) }
            composable(Rutas.INICIO) {
                val vm: InicioViewModel = viewModel(key = "inicio-${usuario?.id}", factory = VetViewModelFactory(app, usuario?.id ?: 0))
                InicioScreen(usuario?.nombre.orEmpty(), vm, { nav.navigate("ficha/$it") }, { nav.navigate("mascota_form/0") }, { nav.navigate("pedir_cita/0") })
            }
            composable(Rutas.MASCOTAS) {
                val vm: InicioViewModel = viewModel(key = "mascotas-${usuario?.id}", factory = VetViewModelFactory(app, usuario?.id ?: 0))
                MascotasScreen(vm, { nav.navigate("ficha/$it") }, { nav.navigate("mascota_form/0") })
            }
            composable(Rutas.CITAS) {
                val vm: MisCitasViewModel = viewModel(key = "citas-${usuario?.id}", factory = VetViewModelFactory(app, usuario?.id ?: 0))
                MisCitasScreen(vm) { nav.navigate("pedir_cita/0") }
            }
            composable(Rutas.PANEL) {
                val vm: PanelViewModel = viewModel(factory = VetViewModelFactory(app))
                PanelScreen(vm) { nav.navigate(Rutas.AGENDA) }
            }
            composable(Rutas.AGENDA) {
                val vm: AgendaViewModel = viewModel(factory = VetViewModelFactory(app))
                AgendaScreen(vm) { nav.navigate("atender/$it") }
            }
            composable(Rutas.ATENDER) { destino ->
                val id = destino.arguments?.getString("id")?.toIntOrNull() ?: 0
                val vm: AtenderCitaViewModel = viewModel(key = "atender-$id", factory = VetViewModelFactory(app, itemId = id))
                AtenderCitaScreen(vm) { nav.popBackStack() }
            }
            composable(Rutas.PACIENTES) {
                val vm: PacientesViewModel = viewModel(factory = VetViewModelFactory(app))
                PacientesScreen(vm) { nav.navigate("ficha/$it") }
            }
            composable(Rutas.SERVICIOS) {
                val vm: ServiciosViewModel = viewModel(factory = VetViewModelFactory(app))
                ServiciosScreen(vm) { nav.navigate("servicio_form/$it") }
            }
            composable(Rutas.FORM_SERVICIO) { destino ->
                val id = destino.arguments?.getString("id")?.toIntOrNull() ?: 0
                val vm: ServiciosViewModel = viewModel(key = "servicios-form", factory = VetViewModelFactory(app))
                FormularioServicioScreen(vm, id) { nav.popBackStack() }
            }
            composable(Rutas.PERFIL) {
                val vm: PerfilViewModel = viewModel(key = "perfil-${usuario?.id}", factory = VetViewModelFactory(app, usuario?.id ?: 0))
                PerfilScreen(usuario?.nombre.orEmpty(), usuario?.correo.orEmpty(), vm, sesion)
            }
            composable(Rutas.AJUSTES) {
                val vm: PerfilViewModel = viewModel(key = "ajustes-${usuario?.id}", factory = VetViewModelFactory(app, usuario?.id ?: 0))
                AjustesScreen(usuario?.nombre.orEmpty(), usuario?.correo.orEmpty(), vm, sesion)
            }
            composable(Rutas.FICHA) { destino ->
                val id = destino.arguments?.getString("id")?.toIntOrNull() ?: 0
                val vm: MascotaViewModel = viewModel(key = "ficha-$id", factory = VetViewModelFactory(app, usuario?.id ?: 0, id))
                FichaMascotaScreen(vm, usuario?.rol == "CLIENTE", { nav.navigate("mascota_form/$id") }, { nav.navigate("pedir_cita/$id") }, { nav.popBackStack() })
            }
            composable(Rutas.FORM_MASCOTA) { destino ->
                val id = destino.arguments?.getString("id")?.toIntOrNull() ?: 0
                val vm: MascotaViewModel = viewModel(key = "form-$id", factory = VetViewModelFactory(app, usuario?.id ?: 0, id))
                FormularioMascotaScreen(vm) { nav.popBackStack() }
            }
            composable(Rutas.PEDIR_CITA) { destino ->
                val id = destino.arguments?.getString("id")?.toIntOrNull() ?: 0
                val vm: PedirCitaViewModel = viewModel(key = "pedir-$id", factory = VetViewModelFactory(app, usuario?.id ?: 0, id))
                PedirCitaScreen(vm) { nav.navigate(Rutas.CITAS) { popUpTo(Rutas.INICIO) } }
            }
        }
    }
}
