package com.saludplus.paciente.ui.screens.auth

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.theme.AzulClinico

@Composable
/** Portada de bienvenida con acceso a registro o a la cuenta de demostración. */
fun SplashScreen(onRegister: () -> Unit, onLogin: () -> Unit) {
    val context = LocalContext.current
    val portada = remember {
        runCatching {
            context.assets.open("clinic_cover.png").use { BitmapFactory.decodeStream(it).asImageBitmap() }
        }.getOrNull()
    }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxWidth().height(350.dp).clip(RoundedCornerShape(28.dp))
                    .background(AzulClinico),
                contentAlignment = Alignment.BottomStart
            ) {
                if (portada != null) {
                    Image(portada, contentDescription = "Portada ilustrada de Clínica SaludPlus", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.28f)))
                }
                Column(Modifier.fillMaxWidth().padding(22.dp)) {
                    Text("Clínica SaludPlus", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Tu salud, más cerca", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text("Encuentra especialistas y agenda tu próxima cita de forma sencilla.", color = Color.White)
                }
            }
        }
        SaludPlusButton("Crear una cuenta", onRegister)
        Spacer(Modifier.height(12.dp))
        androidx.compose.material3.OutlinedButton(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Ya tengo una cuenta")
        }
    }
}

