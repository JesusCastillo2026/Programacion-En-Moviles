package com.jesuscastillo.registronotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesuscastillo.registronotas.ui.theme.MyApplicationTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    RegistroNotasScreen(Modifier.padding(padding))
                }
            }
        }
    }
}

private data class Curso(val nombre: String, val peso: Float)

private val cursos = listOf(
    Curso("Fundamentos de Programación", 0.20f),
    Curso("Programación Orientada a Objetos", 0.25f),
    Curso("Programación en Móviles", 0.30f),
    Curso("Base de Datos", 0.25f)
)

@Composable
fun RegistroNotasScreen(modifier: Modifier = Modifier) {
    var notas by remember { mutableStateOf(List(4) { 0f }) }
    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }
    var calculado by remember { mutableStateOf(false) }

    val ponderado = notas.zip(cursos).sumOf { (nota, curso) -> nota.toDouble() * curso.peso }
    val promedioFinal = if (redondear) ponderado.roundToInt().toDouble() else ponderado
    val observacion = when {
        promedioFinal >= 17 -> "EXCELENTE"
        promedioFinal >= 13 -> "APROBADO"
        promedioFinal >= 10 -> "EN RECUPERACIÓN"
        else -> "DESAPROBADO"
    }
    val chipColor = when (observacion) {
        "EXCELENTE" -> Color(0xFF176B45)
        "APROBADO" -> Color(0xFF2E7D32)
        "EN RECUPERACIÓN" -> Color(0xFFB7791F)
        else -> Color(0xFFB3261E)
    }

    Column(
        modifier = modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFF6F1FF), Color(0xFFFFFFFF)))
        ).padding(horizontal = 20.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Registro de Notas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Asigna las notas de tus cursos y calcula tu promedio ponderado.", color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(8.dp))

        cursos.forEachIndexed { index, curso ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${curso.nombre} (${(curso.peso * 100).roundToInt()}%)", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Surface(color = if (notas[index] >= 13f) Color(0xFFDDF5E7) else Color(0xFFFFE1E1), shape = RoundedCornerShape(12.dp)) {
                        Text("${notas[index].roundToInt()}", Modifier.padding(horizontal = 12.dp, vertical = 5.dp), fontWeight = FontWeight.Bold)
                    }
                }
                Slider(value = notas[index], onValueChange = { value ->
                    notas = notas.toMutableList().also { it[index] = value.roundToInt().toFloat() }
                    calculado = false
                }, valueRange = 0f..20f, steps = 19)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Redondear promedio final")
            Switch(checked = redondear, onCheckedChange = { redondear = it; calculado = false })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = confirmado, onCheckedChange = { confirmado = it; calculado = false })
            Text("Confirmo que las notas son correctas")
        }
        Button(onClick = { calculado = true }, enabled = confirmado, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors()) {
            Text("CALCULAR PROMEDIO")
        }
        TextButton(onClick = { notas = List(4) { 0f }; redondear = false; confirmado = false; calculado = false }, modifier = Modifier.fillMaxWidth()) {
            Text("LIMPIAR")
        }

        if (!calculado) {
            Text("Asigna las notas y confirma para calcular", color = MaterialTheme.colorScheme.outline, fontSize = 13.sp)
        } else {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Resultado final", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Promedio ponderado: ${"%.2f".format(ponderado)}")
                    Text("Promedio final: ${if (redondear) promedioFinal.toInt() else "%.2f".format(promedioFinal)}${if (redondear) " (redondeado)" else ""}", fontWeight = FontWeight.Bold)
                    Surface(color = chipColor, shape = RoundedCornerShape(20.dp)) {
                        Text(observacion, Modifier.padding(horizontal = 14.dp, vertical = 7.dp), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("✓ Promedio calculado correctamente", color = Color(0xFF2E7D32))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Text("Desarrollado por: Jesús Castillo Sumire", modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp), color = MaterialTheme.colorScheme.outline, fontSize = 12.sp)
    }
}
