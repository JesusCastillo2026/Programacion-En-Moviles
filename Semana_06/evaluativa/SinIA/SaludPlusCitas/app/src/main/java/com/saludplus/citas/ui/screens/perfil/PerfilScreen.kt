package com.saludplus.citas.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonPrincipal
import com.saludplus.citas.ui.components.TituloSeccion

@Composable
fun PerfilScreen(onCerrarSesion: () -> Unit, onTerminos: () -> Unit) {
    val usuario = Repositorio.usuarioActual
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TituloSeccion("Mis datos")
        Card {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Nombre: ${usuario?.nombre ?: "No disponible"}")
                Text("Correo: ${usuario?.correo ?: "No disponible"}")
                Text("Citas: ${usuario?.let { Repositorio.citasDelUsuario(it.id).size } ?: 0}")
            }
        }
        TextButton(onClick = onTerminos) { Text("Términos y condiciones") }
        BotonPrincipal("Cerrar sesión") {
            Repositorio.cerrarSesion()
            onCerrarSesion()
        }
    }
}
