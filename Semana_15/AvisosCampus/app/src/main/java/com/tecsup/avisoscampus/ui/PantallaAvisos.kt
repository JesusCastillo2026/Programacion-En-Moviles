package com.tecsup.avisoscampus.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.tecsup.avisoscampus.notificacion.Notificador
import com.tecsup.avisoscampus.model.PermisoUi
import com.tecsup.avisoscampus.viewmodel.AvisosViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        vm.permisoRespondido(it)
    }

    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }
    LaunchedEffect(vm) {
        vm.eventos.collect { aviso ->
            if (!Notificador.mostrar(contexto, aviso)) vm.noSePudoMostrar()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("AvisosCampus") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardSection("Permiso de notificaciones") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estado:")
                    AssistChip(onClick = {}, label = { Text(when (permiso) {
                        PermisoUi.SinPedir -> "Sin pedir"
                        PermisoUi.Concedido -> "Permitido"
                        PermisoUi.Denegado -> "Denegado"
                    }) })
                }
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= 33) lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else vm.permisoRespondido(true)
                }) { Text("Permitir notificaciones") }
            }
            CardSection("Avisos") { Button(onClick = vm::avisarAhora) { Text("Mostrar aviso") } }
            CardSection("Recordatorio") { Button(onClick = {}) { Text("Recordar en 10 s") } }
            CardSection("Receptor de anuncios") { Text("Escuchar el modo avión") }
            CardSection("Historial") { Text("Sin avisos todavía") }
        }
    }
}

@Composable
private fun CardSection(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = {
            Text(titulo, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
            content()
        })
    }
}
