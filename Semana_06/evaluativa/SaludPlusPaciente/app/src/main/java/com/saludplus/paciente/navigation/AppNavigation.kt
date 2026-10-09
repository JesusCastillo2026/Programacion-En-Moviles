package com.saludplus.paciente.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.screens.auth.LoginScreen
import com.saludplus.paciente.ui.screens.auth.RegistroScreen
import com.saludplus.paciente.ui.screens.auth.SplashScreen
import com.saludplus.paciente.ui.screens.auth.TerminosScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {
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
        composable(Rutas.TERMINOS) {
            TerminosScreen(onBack = { navController.popBackStack() })
        }
        composable(Rutas.INICIO) {
            PantallaInicioTemporal(onContinue = { navController.navigate(Rutas.ESPECIALIDADES) })
        }
    }
}

private fun androidx.navigation.NavHostController.irAInicio() {
    navigate(Rutas.INICIO) {
        popUpTo(Rutas.SPLASH) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun PantallaInicioTemporal(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hola, ${Repositorio.usuarioActual?.nombre.orEmpty()}", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onContinue) { Text("Explorar especialidades") }
    }
}

