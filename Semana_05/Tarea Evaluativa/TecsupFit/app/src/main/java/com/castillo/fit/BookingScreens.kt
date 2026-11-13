package com.castillo.fit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailScreen(item: Item, onAction: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(16.dp)) {
            Symbol(if(clinic) Modifier.size(88.dp).align(Alignment.CenterHorizontally) else Modifier.fillMaxWidth().height(120.dp))
            Text(item.name, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text(item.subtitle)
            if(clinic) Text("★ ${item.rating} (128 reseñas)")
            Text(item.description)
            if(!clinic) Text("8 de ${item.capacity} cupos disponibles")
        }
        PrimaryButton(if(clinic) "Agendar cita" else "Reservar cupo", onClick=onAction)
    }
}

@Composable
fun ChoiceRow(options: List<String>, value: String, onChoose: (String) -> Unit) {
    // Un valor por grupo asegura exclusión mutua y semántica accesible de RadioButton.
    Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            Surface(color=if(value==option) accent else cardColor,
                contentColor=if(value==option) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.DarkGray,
                shape=androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                modifier=Modifier.weight(1f).selectable(value==option, role=Role.RadioButton, onClick={onChoose(option)})) {
                Box(Modifier.heightIn(min=52.dp).padding(8.dp), contentAlignment=Alignment.Center) {Text(option)}
            }
        }
    }
}

@Composable
fun BookingScreen(item: Item, bookings: List<Booking>, onConfirm: (String, String) -> Unit) {
    // Las claves evitan arrastrar selecciones al abrir otro médico o clase.
    var date by rememberSaveable(item.id) { mutableStateOf("") }
    var time by rememberSaveable(item.id) { mutableStateOf("") }
    val dateOptions = if(clinic) dates else listOf("Hoy", "Mañana", "Sábado")
    val duplicate = isDuplicate(bookings,item,date,time)
    val seats = availableSeats(bookings,item,date,time)
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(16.dp)) {
            if(enhanced) Text(item.name, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text("Selecciona fecha")
            ChoiceRow(dateOptions,date) {date=it}
            Text("Selecciona ${if(clinic) "hora" else "horario"}")
            ChoiceRow(timesFor(item),time) {time=it}
            if(!clinic && time.isNotBlank()) Text("$seats cupos disponibles")
            if(duplicate) Text("Ya tienes una reserva para esta fecha y hora.", color=MaterialTheme.colorScheme.error)
            if(date.isBlank() || time.isBlank()) Text("Elige una fecha y una hora para continuar.")
        }
        PrimaryButton(if(clinic) "Confirmar cita" else "Confirmar reserva",
            date.isNotBlank() && time.isNotBlank() && !duplicate && (clinic || seats>0)) {onConfirm(date,time)}
    }
}

@Composable
fun ConfirmationScreen(booking: Booking?, onBookings: () -> Unit, onHome: () -> Unit) {
    // El resumen usa el registro recién creado, no campos globales editables.
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement=Arrangement.Center,
        horizontalAlignment=Alignment.CenterHorizontally) {
        Symbol(Modifier.size(80.dp), success=true)
        Spacer(Modifier.height(24.dp))
        Text(if(clinic) "¡Cita agendada!" else "¡Cupo reservado!", fontSize=24.sp, fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(booking?.name ?: "Reserva no encontrada")
        booking?.let {Text("${it.date}, ${it.time}")}
        Spacer(Modifier.height(36.dp))
        FilledTonalButton(onClick=onBookings) {Text(if(clinic) "Ver mis citas" else "Ver mis reservas")}
        TextButton(onClick=onHome) {Text("Volver al inicio")}
    }
}
