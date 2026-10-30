package com.saludplus.paciente.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.saludplus.paciente.ui.theme.*

/** Portada común: la imagen ocupa el ancho y se funde con el fondo, sin marco rectangular. */
@Composable
fun AuthLayout(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    welcome: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current
    val portada = remember(context) {
        runCatching {
            context.assets.open("clinic_cover.png").use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
        }.getOrNull()
    }
    Column(
        Modifier.fillMaxSize().background(FondoClinico).imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Box(Modifier.fillMaxWidth().height(if (welcome) 340.dp else 210.dp).background(CelesteSuave)) {
            if (portada != null) Image(
                bitmap = portada, contentDescription = null,
                contentScale = ContentScale.Crop, alignment = BiasAlignment(0f, -0.35f),
                modifier = Modifier.matchParentSize()
            )
            // La transición inferior integra la ilustración con el contenido legible del formulario.
            Box(Modifier.matchParentSize().background(Brush.verticalGradient(
                0f to Color.White.copy(alpha = 0.18f),
                0.48f to Color.Transparent,
                1f to FondoClinico
            )))
            Row(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (onBack != null) {
                    FilledTonalIconButton(onClick = onBack, shape = CircleShape,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White.copy(alpha = 0.95f))) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                } else Spacer(Modifier.width(1.dp))
                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.94f)) {
                    Text("✚  SALUDPLUS", Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = AzulProfundo, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
        Column(
            Modifier.align(Alignment.CenterHorizontally).widthIn(max = 560.dp).fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = {
                Text("TU ESPACIO DE SALUD", color = AzulClinico,
                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextoSecundario, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(2.dp))
                content()
                Spacer(Modifier.height(24.dp))
            }
        )
    }
}

/** Campo uniforme con icono, ayuda, error y opción accesible para mostrar la contraseña. */
@Composable
fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    error: String? = null,
    hint: String? = null
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = true, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType),
        visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (password) {
            { IconButton(onClick = { visible = !visible }) {
                Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña")
            } }
        } else null,
        isError = error != null,
        supportingText = if (error != null || hint != null) { { Text(error ?: hint.orEmpty()) } } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

/** Avisos comprensibles para los errores de formulario o de inicio de sesión. */
@Composable
fun FormError(message: String) {
    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(14.dp),
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
        Text(message, Modifier.fillMaxWidth().padding(14.dp),
            color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
    }
}

