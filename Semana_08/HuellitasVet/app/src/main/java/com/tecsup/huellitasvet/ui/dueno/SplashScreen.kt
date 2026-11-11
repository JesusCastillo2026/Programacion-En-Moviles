package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.ui.components.MascotaAvatar

@Composable
fun SplashScreen(cargando: Boolean, onComenzar: () -> Unit, onYaTengoCuenta: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val primary = MaterialTheme.colorScheme.primary
            Spacer(Modifier.weight(1f))
            Canvas(Modifier.size(104.dp)) {
                drawRoundRect(primary, cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()))
                drawCircle(Color.White, radius = size.width * .18f, center = Offset(size.width * .5f, size.height * .62f))
                listOf(.25f, .42f, .60f, .77f).forEachIndexed { index, x ->
                    drawCircle(Color.White, radius = size.width * .075f, center = Offset(size.width * x, size.height * if (index % 2 == 0) .34f else .26f))
                }
            }
            Text("HuellitasVet", Modifier.padding(top = 18.dp), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text("Cuidamos a tu mejor amigo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(.55f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)) {
                MascotaAvatar("PERRO", tamano = 62.dp)
                MascotaAvatar("GATO", tamano = 62.dp)
                MascotaAvatar("OTRO", tamano = 62.dp)
            }
            Spacer(Modifier.weight(.8f))
            if (cargando) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(onClick = onComenzar, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Comenzar") }
                OutlinedButton(onClick = onYaTengoCuenta, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) { Text("Ya tengo una cuenta") }
            }
        }
    }
}
