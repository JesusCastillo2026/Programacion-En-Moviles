package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class RequisitoPassword(val titulo: String, val cumple: Boolean)

private fun revisarPassword(clave: String) = listOf(
    RequisitoPassword("Al menos 8 caracteres", clave.length >= 8),
    RequisitoPassword("Una letra mayúscula", clave.any(Char::isUpperCase)),
    RequisitoPassword("Una letra minúscula", clave.any(Char::isLowerCase)),
    RequisitoPassword("Un número", clave.any(Char::isDigit)),
    RequisitoPassword("Un símbolo (por ejemplo: !, @ o #)", clave.any { !it.isLetterOrDigit() && !it.isWhitespace() }),
    RequisitoPassword("Sin espacios", clave.none(Char::isWhitespace))
)

@Composable
fun RequisitosContrasenaCard(clave: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Requisitos de contraseña", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text("Se actualizan mientras escribes:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            revisarPassword(clave).forEach { requisito ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = if (requisito.cumple) "Requisito cumplido" else "Requisito pendiente",
                        modifier = Modifier.size(18.dp),
                        tint = if (requisito.cumple) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        requisito.titulo,
                        color = if (requisito.cumple) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
