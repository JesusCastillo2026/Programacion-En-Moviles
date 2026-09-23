package com.castillo.clinica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookingsScreen(bookings: List<Booking>, onCancel: (Int) -> Unit = {}) {
    var pending by remember { mutableStateOf<Booking?>(null) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(24.dp),
        verticalArrangement=Arrangement.spacedBy(16.dp)) {
        if(bookings.isEmpty()) item {EmptyMessage("Todavía no hay reservas", "Selecciona una opción en Inicio.")}
        items(bookings, key={it.id}) { booking ->
            Surface(color=cardColor, shape=RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    if(booking.status=="Confirmada") Box(Modifier.width(5.dp).fillMaxHeight().background(accent))
                    Column(Modifier.padding(16.dp), verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Text(booking.name, fontWeight=FontWeight.Bold)
                        Text("${booking.date}, ${booking.time}", fontSize=13.sp)
                        StatusBadge(booking.status)
                        if(enhanced && booking.status=="Confirmada")
                            TextButton(onClick={pending=booking}) {Text("Cancelar ${if(clinic) "cita" else "reserva"}")}
                    }
                }
            }
        }
    }
    pending?.let { booking ->
        AlertDialog(onDismissRequest={pending=null},
            title={Text("¿Cancelar la reserva?")},
            text={Text("${booking.name}\n${booking.date}, ${booking.time}\nEsta acción liberará el horario.")},
            confirmButton={TextButton(onClick={onCancel(booking.id);pending=null}) {Text("Sí, cancelar")}},
            dismissButton={TextButton(onClick={pending=null}) {Text("Mantener reserva")}})
    }
}

@Composable
fun ProfileScreen(bookings: List<Booking>) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(24.dp),
        horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.spacedBy(16.dp)) {
        item { Box(Modifier.size(88.dp).background(tint,CircleShape),contentAlignment=Alignment.Center) {
            Text(if(clinic) "JP" else "DR",fontSize=26.sp,fontWeight=FontWeight.Bold,color=accent)
        } }
        item { Text(person,fontSize=22.sp,fontWeight=FontWeight.Bold); Text(if(clinic) "Paciente" else "Plan Premium") }
        item { Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            listOf((if(enhanced) bookings.count{it.status=="Completada"}.toString() else if(clinic) "1" else "14") to
                (if(clinic) "Atenciones" else "Clases"),
                (if(enhanced) bookings.count{it.status=="Confirmada"}.toString() else "3") to
                (if(enhanced) "Próximas" else "Rachas")).forEach { (value,label) ->
                Column(Modifier.weight(1f).background(cardColor,RoundedCornerShape(16.dp)).padding(20.dp),
                    horizontalAlignment=Alignment.CenterHorizontally) {
                    Text(value,fontSize=24.sp,fontWeight=FontWeight.Bold); Text(label)
                }
            }
        } }
        if(enhanced) item {Text("Estadísticas calculadas a partir de tus reservas de esta sesión.")}
    }
}

@Composable
fun ExtraScreen(bookings: List<Booking>) {
    if(clinic) {
        val completed = bookings.filter {it.status=="Completada"}
        BookingsScreen(completed)
    } else {
        LazyColumn(contentPadding=PaddingValues(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            items(listOf("Movilidad · 10 min" to "Calentamiento suave de articulaciones.",
                "Fuerza · 20 min" to "Tres series de sentadillas y ejercicios adaptados a tu nivel.",
                "Recuperación · 5 min" to "Respiración y estiramientos suaves.")) { (title,body) ->
                Surface(color=cardColor,shape=RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {Text(title,fontWeight=FontWeight.Bold); Text(body)}
                }
            }
        }
    }
}

