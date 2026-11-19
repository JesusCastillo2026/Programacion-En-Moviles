package com.tecsup.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.campusgps.model.TipoError
import com.tecsup.campusgps.model.UbicacionUi

@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(error.mensaje, color = MaterialTheme.colorScheme.error)
        when (error.tipo) {
            TipoError.SIN_PERMISO -> Button(onClick = onOpenSettings) { Text("Abrir ajustes") }
            TipoError.GPS_APAGADO, TipoError.OTRO -> Button(onClick = onRetry) { Text("Reintentar") }
            TipoError.SIN_PLAY_SERVICES -> Unit
        }
    }
}
