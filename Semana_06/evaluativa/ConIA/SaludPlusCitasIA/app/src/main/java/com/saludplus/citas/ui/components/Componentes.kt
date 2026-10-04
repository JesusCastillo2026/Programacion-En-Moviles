package com.saludplus.citas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.theme.AzulClaro

/** Botón consistente para las acciones principales del flujo. */
@Composable
fun BotonPrincipal(texto: String, habilitado: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = habilitado, modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp)) { Text(texto, fontWeight = FontWeight.Bold) }
}

/** Encabezado reutilizado en pantallas de catálogo, cita y perfil. */
@Composable
fun TituloSeccion(texto: String) {
    Text(texto, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
}

/** Mensaje central para listas sin contenido o búsquedas sin coincidencias. */
@Composable
fun EstadoVacio(titulo: String, detalle: String) {
    Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(detalle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Tarjeta de especialidad que entrega su identificador al navegar. */
@Composable
fun TarjetaEspecialidad(especialidad: Especialidad, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(48.dp).background(AzulClaro, CircleShape), contentAlignment = Alignment.Center) {
                Text(especialidad.icono, style = MaterialTheme.typography.titleLarge)
            }
            Column(Modifier.weight(1f)) {
                Text(especialidad.nombre, fontWeight = FontWeight.Bold)
                Text(especialidad.descripcion, style = MaterialTheme.typography.bodySmall)
            }
            Text("›", style = MaterialTheme.typography.titleLarge)
        }
    }
}

/** Resumen del médico con experiencia y calificación para comparar opciones. */
@Composable
fun TarjetaMedico(medico: Medico, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(48.dp).background(AzulClaro, CircleShape), contentAlignment = Alignment.Center) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
            Column(Modifier.weight(1f)) {
                Text(medico.nombre, fontWeight = FontWeight.Bold)
                Text(medico.experiencia, style = MaterialTheme.typography.bodySmall)
            }
            Text("★ ${medico.calificacion}", color = Color(0xFF8B6800))
        }
    }
}

/** Cita compartida entre Mis citas y otras listas del paciente. */
@Composable
fun TarjetaCita(cita: Cita, onClick: () -> Unit) {
    val medico = Repositorio.obtenerMedico(cita.medicoId)
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(medico?.nombre ?: "Médico", fontWeight = FontWeight.Bold)
            Text("${cita.fecha} · ${cita.hora}")
            Text(cita.estado, color = MaterialTheme.colorScheme.primary)
        }
    }
}
