package com.castillo.fit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onDetail: (Int) -> Unit) {
    var filter by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }
    val filters = if(clinic) listOf("Cardiología", "Pediatría", "Dermatología") else listOf("Hoy", "Esta semana")
    val visible = catalog.filter { item ->
        (filter.isBlank() || filter == "Esta semana" || item.category == filter) &&
        (query.isBlank() || item.name.contains(query, true) || item.category.contains(query, true))
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        if(enhanced) item {
            Column(Modifier.fillMaxWidth().background(accent, RoundedCornerShape(24.dp)).padding(24.dp)) {
                Text(if(clinic) "Tu salud, a un paso" else "Hoy es un buen día para moverte",
                    fontSize=26.sp, fontWeight=FontWeight.Bold, color=Color.White)
                Text(if(clinic) "Encuentra atención para ti." else "Elige tu próxima clase.",
                    Modifier.padding(top=8.dp), color=Color.White)
            }
        }
        if(enhanced) item { OutlinedTextField(query, {query=it}, Modifier.fillMaxWidth(),
            label={Text(if(clinic) "Buscar médico o especialidad" else "Buscar clase")},
            singleLine=true, shape=RoundedCornerShape(16.dp)) }
        item { LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            items(filters) { value ->
                FilterChip(selected=filter==value, onClick={ filter=if(filter==value) "" else value },
                    label={Text(value)}, shape=RoundedCornerShape(24.dp))
            }
        } }
        item { Text(if(clinic) "Médicos disponibles" else "Clases disponibles", fontWeight=FontWeight.Bold) }
        items(visible, key={it.id}) { item -> ItemCard(item) {onDetail(item.id)} }
        if(visible.isEmpty()) item { EmptyMessage("Sin resultados", "Prueba otra búsqueda o categoría.") }
    }
}

