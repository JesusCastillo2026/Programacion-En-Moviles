package com.saludplus.paciente.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.components.*

/** Bienvenida con ilustración integrada y acceso directo a ambas opciones de cuenta. */
@Composable
fun SplashScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    AuthLayout(
        title = "Tu salud,\na un paso de ti.",
        subtitle = "Encuentra especialistas, elige tu horario y organiza tus próximas citas.",
        welcome = true
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip("Especialistas")
            StatusChip("Citas a tu ritmo")
        }
        Spacer(Modifier.height(8.dp))
        SaludPlusButton("Comenzar", onRegister)
        OutlinedButton(onClick = onLogin,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = RoundedCornerShape(18.dp)) { Text("Ya tengo una cuenta") }
        Text("Clínica SaludPlus · App Paciente", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

