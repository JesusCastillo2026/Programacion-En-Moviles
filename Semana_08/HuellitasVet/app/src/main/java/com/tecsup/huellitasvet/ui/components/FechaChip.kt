package com.tecsup.huellitasvet.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FechaChip(fecha: LocalDate, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(fecha.format(DateTimeFormatter.ofPattern("EEE d", Locale("es", "PE"))), fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
