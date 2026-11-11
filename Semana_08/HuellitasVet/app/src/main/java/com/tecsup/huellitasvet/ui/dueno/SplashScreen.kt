package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(Modifier.size(96.dp)) {
                drawRoundRect(Color(0xFF059669), cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()))
                drawCircle(Color.White, radius = size.width * .18f, center = Offset(size.width * .5f, size.height * .62f))
                listOf(.25f, .42f, .60f, .77f).forEachIndexed { index, x ->
                    drawCircle(Color.White, radius = size.width * .075f, center = Offset(size.width * x, size.height * if (index % 2 == 0) .34f else .26f))
                }
            }
            Text("HuellitasVet", Modifier.padding(top = 16.dp), style = MaterialTheme.typography.headlineMedium)
            Text("Cuidamos a tu mejor amigo", style = MaterialTheme.typography.bodyMedium)
            CircularProgressIndicator(Modifier.padding(top = 24.dp))
        }
    }
}
