package com.tecsup.avisoscampus.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.model.PermisoUi
import com.tecsup.avisoscampus.notificacion.Notificador
import com.tecsup.avisoscampus.receiver.CargadorReceiver
import com.tecsup.avisoscampus.receiver.ModoAvionReceiver
import com.tecsup.avisoscampus.viewmodel.AvisosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val escuchando by vm.escuchandoAvion.collectAsState()
    val escuchandoCargador by vm.escuchandoCargador.collectAsState()
    val avisos by vm.avisos.collectAsState()
    var segundosElegidos by rememberSaveable { mutableIntStateOf(10) }
    var filtro by rememberSaveable { mutableStateOf("Todos") }
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        vm.permisoRespondido(it)
    }
    val avisosFiltrados = when (filtro) {
        "Botón" -> avisos.filter { it.origen == Origen.BOTON }
        "Recordatorio" -> avisos.filter { it.origen == Origen.RECORDATORIO }
        "Modo avión" -> avisos.filter { it.origen == Origen.MODO_AVION }
        "Cargador" -> avisos.filter { it.origen == Origen.CARGADOR }
        else -> avisos
    }

    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }
    LaunchedEffect(vm) {
        vm.eventos.collect { aviso ->
            if (!Notificador.mostrar(contexto, aviso)) vm.noSePudoMostrar()
        }
    }
    DisposableEffect(contexto, escuchando, vm) {
        if (!escuchando) return@DisposableEffect onDispose { }
        val receiver = ModoAvionReceiver(vm::alCambiarModoAvion)
        ContextCompat.registerReceiver(
            contexto, receiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose { contexto.unregisterReceiver(receiver) }
    }
    DisposableEffect(contexto, escuchandoCargador, vm) {
        if (!escuchandoCargador) return@DisposableEffect onDispose { }
        val receiver = CargadorReceiver(vm::alCambiarCargador)
        val filtroCargador = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        ContextCompat.registerReceiver(contexto, receiver, filtroCargador, ContextCompat.RECEIVER_NOT_EXPORTED)
        onDispose { contexto.unregisterReceiver(receiver) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AvisosCampus", fontWeight = FontWeight.Bold)
                        Text("Notificaciones del dispositivo", style = MaterialTheme.typography.labelSmall)
                    }
                },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text("${avisos.size}") },
                        leadingIcon = { Icon(Icons.Rounded.NotificationsActive, contentDescription = null) }
                    )
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardSection("Permiso de notificaciones", Icons.Rounded.Security) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estado", style = MaterialTheme.typography.bodyMedium)
                    EstadoPermiso(permiso)
                }
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= 33) lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                        else vm.permisoRespondido(true)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Permitir notificaciones") }
                AnimatedVisibility(mensaje != null) {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            mensaje.orEmpty(),
                            Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedButton(onClick = {
                            contexto.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                            )
                        }) { Text("Abrir ajustes") }
                    }
                }
            }

            CardSection("Avisos", Icons.Rounded.NotificationsActive, "Una confirmación rápida en tu dispositivo.") {
                Button(onClick = vm::avisarAhora, modifier = Modifier.fillMaxWidth()) { Text("Mostrar aviso") }
            }

            CardSection("Recordatorio", Icons.Rounded.AccessTime, "Programa un aviso con cuenta regresiva.") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15).forEach { segundos ->
                        FilterChip(
                            selected = segundosElegidos == segundos,
                            onClick = { segundosElegidos = segundos },
                            enabled = faltan == null,
                            label = { Text("$segundos s") }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.recordarEn(segundosElegidos) }, enabled = faltan == null) {
                        Text("Recordar en ${segundosElegidos} s")
                    }
                    OutlinedButton(onClick = vm::cancelarRecordatorio, enabled = faltan != null) { Text("Cancelar") }
                }
                AnimatedVisibility(faltan != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Faltan ${faltan ?: 0} s", Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
                        LinearProgressIndicator(
                            progress = { if (faltan == null) 0f else (segundosElegidos - faltan!!).toFloat() / segundosElegidos },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            CardSection("Receptor de anuncios", Icons.Rounded.AirplanemodeActive, "Escoge qué cambios del dispositivo registrar.") {
                FilaSwitch("Escuchar el modo avión", "Activa o desactiva el modo avión del celular", escuchando, vm::cambiarEscucha)
                HorizontalDivider()
                FilaSwitch("Escuchar estado del cargador", "Registrar conexión y desconexión", escuchandoCargador, vm::cambiarEscuchaCargador, Icons.Rounded.BatteryChargingFull)
            }

            CardSection("Historial", Icons.Rounded.History, "Los avisos recientes se guardan en este dispositivo.") {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Todos", "Botón", "Recordatorio", "Modo avión", "Cargador").forEach { tipo ->
                        FilterChip(selected = filtro == tipo, onClick = { filtro = tipo }, label = { Text(tipo) })
                    }
                }
                HistorialAvisos(avisosFiltrados)
                if (avisos.isNotEmpty()) {
                    TextButton(onClick = vm::limpiarHistorial) {
                        Icon(Icons.Rounded.DeleteOutline, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Borrar historial")
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoPermiso(permiso: PermisoUi) {
    val texto = when (permiso) {
        PermisoUi.SinPedir -> "Sin pedir"
        PermisoUi.Concedido -> "Permitido"
        PermisoUi.Denegado -> "Denegado"
    }
    val color = when (permiso) {
        PermisoUi.SinPedir -> MaterialTheme.colorScheme.surfaceVariant
        PermisoUi.Concedido -> Color(0xFFD8F1E2)
        PermisoUi.Denegado -> MaterialTheme.colorScheme.errorContainer
    }
    Surface(color = color, shape = CircleShape) {
        Text(texto, Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun FilaSwitch(
    titulo: String,
    detalle: String,
    activo: Boolean,
    alCambiar: (Boolean) -> Unit,
    icono: androidx.compose.ui.graphics.vector.ImageVector = Icons.Rounded.AirplanemodeActive
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = activo, onCheckedChange = alCambiar)
    }
}

@Composable
private fun CardSection(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    subtitulo: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
                    Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(9.dp).size(20.dp))
                }
                Column {
                    Text(titulo, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (subtitulo != null) Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            content()
        }
    }
}



