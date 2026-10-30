package com.tecsup.huellitasvet.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

@Composable
fun HoraChip(hora: String, seleccionado: Boolean, ocupado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        enabled = !ocupado,
        onClick = onClick,
        label = { Text(hora, fontSize = 12.sp, textDecoration = if (ocupado) TextDecoration.LineThrough else TextDecoration.None) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
