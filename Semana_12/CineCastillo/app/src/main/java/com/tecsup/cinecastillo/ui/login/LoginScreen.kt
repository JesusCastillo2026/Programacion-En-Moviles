package com.tecsup.cinecastillo.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/** Portada vectorial local: conserva nitidez y funciona sin conexión. */
@Composable
private fun CinemaArtwork() {
    Box(Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(28.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF100B16), Color(0xFF8C1F35))))) {
        Canvas(Modifier.fillMaxSize().semantics { contentDescription = "Ilustración de una sala de cine con pantalla y luces" }) {
            val w=size.width; val h=size.height
            val beam=Path().apply { moveTo(w*.5f,h*.1f);lineTo(w*.05f,h);lineTo(w*.95f,h);close() }
            drawPath(beam,Color(0xFFFFC878).copy(alpha=.12f))
            drawRoundRect(Color(0xFFFFE9E9),Offset(w*.2f,h*.16f),Size(w*.6f,h*.46f),androidx.compose.ui.geometry.CornerRadius(18f))
            val play=Path().apply { moveTo(w*.46f,h*.28f);lineTo(w*.46f,h*.5f);lineTo(w*.58f,h*.39f);close() }
            drawPath(play,Color(0xFFE83B4F))
            for(row in 0..1) for(col in 0..5) drawRoundRect(Color(if(row==0) 0xFFCF805A else 0xFFF1AD72),Offset(w*(.1f+col*.14f),h*(.7f+row*.17f)),Size(w*.11f,h*.14f),androidx.compose.ui.geometry.CornerRadius(12f))
            for(i in 0..6) drawCircle(Color(0xFFFFC878),3f,Offset(w*(.08f+i*.14f),h*.08f))
        }
    }
}

@Composable
fun LoginScreen(busy: Boolean, error: String?, onLogin: (String, String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Text("CINE CASTILLO", style=MaterialTheme.typography.labelLarge, color=MaterialTheme.colorScheme.primary, fontWeight=FontWeight.Bold)
        CinemaArtwork()
        Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Text("Tu próxima gran historia", style=MaterialTheme.typography.headlineMedium, color=MaterialTheme.colorScheme.onBackground, fontWeight=FontWeight.Bold)
            Text("Descubre películas que te acompañan. Guarda las que quieres volver a ver.", color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(shape=RoundedCornerShape(24.dp),color=MaterialTheme.colorScheme.surface,tonalElevation=1.dp) {
            Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text("Bienvenido de nuevo",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.SemiBold)
                OutlinedTextField(email,{email=it},label={Text("Correo electrónico")},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth())
                OutlinedTextField(password,{password=it},label={Text("Contraseña")},singleLine=true,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth())
                if(error!=null) Text(error,color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodySmall)
                Button(onClick={onLogin(email,password)},enabled=!busy,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().height(52.dp)) {
                    if(busy) CircularProgressIndicator(Modifier.size(22.dp),strokeWidth=2.dp) else Text("Iniciar sesión",fontWeight=FontWeight.Bold)
                }
            }
        }
        Text("Las cuentas las habilita el administrador del proyecto.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        Text("EXPLORA · DESCUBRE · GUARDA",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary,modifier=Modifier.align(Alignment.CenterHorizontally))
    }
}

