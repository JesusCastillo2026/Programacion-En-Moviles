package com.tecsup.huellitasvet.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun VetTextField(valor: String, onCambio: (String) -> Unit, etiqueta: String, modifier: Modifier = Modifier, secreto: Boolean = false) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        modifier = modifier,
        singleLine = true,
        visualTransformation = if (secreto) PasswordVisualTransformation() else VisualTransformation.None
    )
}
