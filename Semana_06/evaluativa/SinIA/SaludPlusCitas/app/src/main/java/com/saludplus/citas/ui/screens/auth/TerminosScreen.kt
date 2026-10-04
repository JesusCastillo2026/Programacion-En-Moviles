package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.ui.components.TituloSeccion

@Composable
fun TerminosScreen(onVolver: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Spacer(Modifier.height(24.dp))
        TituloSeccion("Términos y condiciones")
        Text("Esta aplicación es una demostración académica de agendamiento de citas.")
        Text("Los datos de usuarios y citas se guardan únicamente en la memoria de la aplicación durante la sesión.")
        Text("No ingreses información médica real. No hay conexión con una clínica ni con una base de datos.")
        Text("Los resultados mostrados son ejemplos y no representan diagnósticos.")
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
    }
}
