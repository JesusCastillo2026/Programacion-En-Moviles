package com.saludplus.paciente.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
/** Explica el alcance académico y los límites de privacidad de la aplicación local. */
fun TerminosScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Términos y condiciones", onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Uso de la aplicación", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Clínica SaludPlus es una aplicación académica de demostración para explorar especialidades y organizar citas ficticias.", color = TextoSecundario)
            Text("Datos de prueba", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("La información de registro, perfil y citas se conserva únicamente en memoria mientras la aplicación está abierta. No ingreses información sensible ni uses esta demostración para atender una necesidad médica real.", color = TextoSecundario)
            Text("Atención médica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Las fichas, médicos y horarios son datos de ejemplo. La aplicación no confirma citas con un centro de salud ni reemplaza la orientación de profesionales médicos.", color = TextoSecundario)
            Text("Privacidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Esta versión no transmite los datos a internet ni utiliza una base de datos. Al cerrar la app, los datos de prueba vuelven a su estado inicial.", color = TextoSecundario)
        }
    }
}

