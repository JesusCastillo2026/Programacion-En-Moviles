package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.citas.ui.components.BotonPrincipal

@Composable
fun SplashScreen(onRegistro: () -> Unit, onLogin: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("✚", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("Clínica SaludPlus", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Tu salud, más cerca de ti")
        Spacer(Modifier.height(48.dp))
        BotonPrincipal("Crear cuenta", onClick = onRegistro)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("Iniciar sesión") }
    }
}
