package com.saludplus.paciente.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.saludplus.paciente.data.model.Medico

/** Muestra el retrato ilustrativo correspondiente a cada médico; las fotos no son identidades reales. */
@Composable
fun FotoMedico(medico: Medico, tamano: Dp) {
    val context = LocalContext.current
    val imagen = remember(context, medico.id) {
        runCatching {
            val archivo = if (medico.indiceImagen < 4) "doctor_portraits.png" else "doctor_m${medico.indiceImagen + 1}.png"
            context.assets.open(archivo).use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
        }.getOrNull()
    }
    if (imagen == null) {
        PlaceholderAvatar(medico.nombre, tamano)
        return
    }
    val lado = imagen.width / 2
    val painter = if (medico.indiceImagen < 4) {
        BitmapPainter(imagen,
            srcOffset = IntOffset((medico.indiceImagen % 2) * lado, (medico.indiceImagen / 2) * lado),
            srcSize = IntSize(lado, lado))
    } else BitmapPainter(imagen)
    Image(painter = painter, contentDescription = "Retrato ilustrativo de ${medico.nombre}",
        contentScale = ContentScale.Crop, modifier = Modifier.size(tamano).clip(CircleShape))
}
