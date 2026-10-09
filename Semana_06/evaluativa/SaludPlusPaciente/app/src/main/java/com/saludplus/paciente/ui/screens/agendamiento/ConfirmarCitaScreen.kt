package com.saludplus.paciente.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.data.repository.Repositorio
import com.saludplus.paciente.data.model.ErrorReserva
import com.saludplus.paciente.ui.components.ReservaProgress
import com.saludplus.paciente.ui.components.FormError
import com.saludplus.paciente.ui.components.AppBackTopBar
import com.saludplus.paciente.ui.components.SaludPlusButton
import com.saludplus.paciente.ui.components.SoftCard
import com.saludplus.paciente.ui.theme.AzulClinico
import com.saludplus.paciente.ui.theme.TextoSecundario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
/** Revisa los datos seleccionados y solicita la creación de la cita al repositorio. */
fun ConfirmarCitaScreen(
    especialidadId: String,
    medicoId: String,
    fecha: String,
    hora: String,
    onBack: () -> Unit,
    onConfirm: (String) -> String?,
    onLogin: () -> Unit = onBack,
    onChooseDoctor: () -> Unit = onBack,
    motivoInicial: String = "",
    onMotivoChange: (String) -> Unit = {}
) {
    var motivo by rememberSaveable { mutableStateOf(motivoInicial) }
    var error by rememberSaveable { mutableStateOf<ErrorReserva?>(null) }
    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = Repositorio.obtenerEspecialidad(especialidadId)
    val fechaTexto = runCatching {
        LocalDate.parse(fecha).format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale("es", "PE")))
    }.getOrDefault(fecha)

    Column(modifier = Modifier.fillMaxSize()) {
        AppBackTopBar("Confirmar cita", onBack)
        Column(modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ReservaProgress(4)
            Text("Revisa los detalles", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Tu cita estará lista al confirmar.", color = TextoSecundario)
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResumenLinea("Especialidad", especialidad?.nombre ?: "—")
                    ResumenLinea("Profesional", medico?.nombre ?: "—")
                    ResumenLinea("Fecha", fechaTexto.replaceFirstChar { it.uppercase(Locale("es", "PE")) })
                    ResumenLinea("Hora", hora)
                }
            }
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it; onMotivoChange(it); error = null },
                label = { Text("Motivo de consulta (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(16.dp)
            )
            error?.let { causa ->
                FormError(causa.mensaje)
                val volverALogin = causa == ErrorReserva.SIN_SESION
                val cambiarMedico = causa in listOf(ErrorReserva.ESPECIALIDAD_INVALIDA, ErrorReserva.MEDICO_INVALIDO, ErrorReserva.ESPECIALIDAD_NO_COINCIDE)
                SaludPlusButton(
                    if (volverALogin) "Iniciar sesión" else if (cambiarMedico) "Elegir especialidad y médico" else "Elegir otro horario",
                    onClick = if (volverALogin) onLogin else if (cambiarMedico) onChooseDoctor else onBack
                )
            }
            Spacer(Modifier.height(12.dp))
            SaludPlusButton("Confirmar cita", onClick = {
                val dia = runCatching { LocalDate.parse(fecha) }.getOrNull()
                error = Repositorio.validarReserva(especialidadId, medicoId, dia, hora)
                if (error == null && onConfirm(motivo) == null) {
                    error = Repositorio.validarReserva(especialidadId, medicoId, dia, hora) ?: ErrorReserva.NO_CONFIRMADA
                }
            })
        }
    }
}

@Composable
private fun ResumenLinea(titulo: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, color = TextoSecundario, style = MaterialTheme.typography.labelMedium)
        Text(valor, color = AzulClinico, fontWeight = FontWeight.SemiBold)
    }
}

