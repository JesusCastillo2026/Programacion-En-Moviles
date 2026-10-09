package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.saludplus.paciente.data.model.Medico
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.PageHeading
import com.saludplus.paciente.ui.components.PlaceholderAvatar
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.components.SearchField
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario

@Composable
/** Presenta médicos filtrables y envía su identificador al selector de turnos. */
fun MedicosScreen(especialidadId: String, onBack: () -> Unit, onSelect: (String) -> Unit) {
    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    var consulta by remember { mutableStateOf("") }
    val resultados = Repositorio.buscarMedicos(especialidadId, consulta)

    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Médicos", onBack)
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            PageHeading(especialidad?.nombre ?: "Especialistas", "Elige un profesional para continuar")
            SearchField(consulta, { consulta = it }, "Buscar médico", Modifier.padding(top = 14.dp, bottom = 12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                items(resultados, key = { it.id }) { medico ->
                    MedicoItem(medico, onClick = { onSelect(medico.id) })
                }
                if (resultados.isEmpty()) {
                    item { Text("No hay médicos que coincidan con tu búsqueda.", color = TextoSecundario, modifier = Modifier.padding(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MedicoItem(medico: Medico, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember {
        runCatching {
            context.assets.open("doctor_portraits.png").use { BitmapFactory.decodeStream(it).asImageBitmap() }
        }.getOrNull()
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (bitmap != null) ImagenMedico(bitmap, medico.indiceImagen)
                else PlaceholderAvatar(medico.nombre, 58.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(medico.nombre, fontWeight = FontWeight.Bold)
                    Text(medico.especialidadNombre, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
                    Text("${medico.experiencia} años de experiencia", color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFE5A400))
                    Text("${medico.calificacion}", color = AzulClinico, fontWeight = FontWeight.SemiBold)
                }
            }
            SaludPlusButton("Ver horarios", onClick = onClick)
        }
    }
}

/** Recorta uno de los cuatro retratos ficticios incluidos en la hoja de recursos local. */
@Composable
private fun ImagenMedico(bitmap: ImageBitmap, indice: Int) {
    val lado = 627
    val columna = indice % 2
    val fila = (indice / 2) % 2
    Image(
        painter = BitmapPainter(
            image = bitmap,
            srcOffset = IntOffset(columna * lado, fila * lado),
            srcSize = IntSize(lado, lado)
        ),
        contentDescription = "Retrato ilustrativo de profesional de salud",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(58.dp).clip(CircleShape)
    )
}


