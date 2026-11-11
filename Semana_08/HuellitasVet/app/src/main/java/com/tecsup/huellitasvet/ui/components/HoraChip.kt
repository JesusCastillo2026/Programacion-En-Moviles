package com.tecsup.huellitasvet.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun HoraChip(hora: String, seleccionado: Boolean, ocupado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        enabled = !ocupado,
        onClick = onClick,
        label = { Text(hora, textDecoration = if (ocupado) TextDecoration.LineThrough else TextDecoration.None) }
    )
}
