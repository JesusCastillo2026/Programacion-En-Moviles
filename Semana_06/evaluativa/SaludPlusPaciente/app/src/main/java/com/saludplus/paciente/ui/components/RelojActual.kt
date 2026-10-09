package com.saludplus.paciente.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import com.saludplus.paciente.data.model.RelojClinica

/** Refresca las pantallas activas cuando avanza la hora del dispositivo. */
@Composable
fun recordarHoraActual(): State<LocalDateTime> = produceState(initialValue = RelojClinica.ahora()) {
    while (true) {
        delay(15_000)
        value = RelojClinica.ahora()
    }
}
