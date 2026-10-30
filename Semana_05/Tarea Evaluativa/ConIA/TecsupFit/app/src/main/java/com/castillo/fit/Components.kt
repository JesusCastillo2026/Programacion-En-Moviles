package com.castillo.fit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val accent = Color(if (clinic) 0xFF5B2A83 else 0xFF10715C)
val tint = Color(if (clinic) 0xFFEEE6F6 else 0xFFDFF5ED)
val cardColor = Color(if (clinic) 0xFFF3F0F7 else 0xFFF0F0F0)
// Activa la presentación enriquecida sin introducir ViewModel ni MVVM.
const val enhanced = true

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    // Una paleta por tema conserva la identidad de la versión de referencia.
    MaterialTheme(colorScheme = lightColorScheme(primary = accent, onPrimary = Color.White,
        primaryContainer = tint, background = Color.White, surface = Color.White,
        onSurface = Color(0xFF222222)), content = content)
}

@Composable
fun Symbol(modifier: Modifier = Modifier, success: Boolean = false) {
    Box(modifier.background(if(success) Color(0xFFDFF5ED) else tint, CircleShape).padding(12.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val color = if(success) Color(0xFF16A085) else accent
            val w = size.width; val h = size.height
            if(success) {
                drawLine(color, Offset(w*.15f,h*.5f), Offset(w*.4f,h*.75f), 5.dp.toPx())
                drawLine(color, Offset(w*.4f,h*.75f), Offset(w*.85f,h*.2f), 5.dp.toPx())
            } else if(clinic) {
                drawLine(color, Offset(w*.5f,h*.15f), Offset(w*.5f,h*.85f), 5.dp.toPx())
                drawLine(color, Offset(w*.15f,h*.5f), Offset(w*.85f,h*.5f), 5.dp.toPx())
            } else {
                drawLine(color, Offset(w*.15f,h*.5f), Offset(w*.85f,h*.5f), 5.dp.toPx())
                drawLine(color, Offset(w*.15f,h*.2f), Offset(w*.15f,h*.8f), 7.dp.toPx())
                drawLine(color, Offset(w*.85f,h*.2f), Offset(w*.85f,h*.8f), 7.dp.toPx())
            }
        }
    }
}

@Composable
fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().heightIn(min=52.dp), enabled = enabled,
        shape = RoundedCornerShape(14.dp)) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(16.dp),
        color = if(enhanced) Color.White else cardColor,
        tonalElevation = if(enhanced) 2.dp else 0.dp) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Symbol(Modifier.size(48.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text(if(clinic) item.subtitle.substringBefore(" ·") else item.subtitle,
                    fontSize = 12.sp, color = Color.DarkGray)
            }
            if(clinic) Text("★ ${item.rating}", fontSize=12.sp, color=Color(0xFF937000))
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val active = status == "Confirmada"
    Text(status, modifier = Modifier.background(if(active) Color(0xFFDFF5ED) else Color(0xFFE5E5E5),
        RoundedCornerShape(20.dp)).padding(horizontal=12.dp, vertical=4.dp),
        fontSize=12.sp, color=if(active) Color(0xFF087C60) else Color.DarkGray)
}

@Composable
fun EmptyMessage(title: String, body: String) {
    Column(Modifier.fillMaxWidth().padding(vertical=32.dp), horizontalAlignment=Alignment.CenterHorizontally) {
        Text(title, fontWeight=FontWeight.Bold)
        Text(body, Modifier.padding(top=8.dp), color=Color.Gray)
    }
}
