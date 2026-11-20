package com.tecsup.campusgps.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.campusgps.R
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.RutaPeatonal
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.util.formatearDistancia
import com.tecsup.campusgps.viewmodel.MapaViewModel
import kotlinx.coroutines.launch

private val GlassSurface = Color(0xE8131E24)
private val GlassBorder = Color.White.copy(alpha = 0.14f)

@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val context = LocalContext.current
    var mapaOscuro by rememberSaveable { mutableStateOf(false) }
    var iniciarSeguimientoAlDarPermiso by remember { mutableStateOf(false) }
    val estado by vm.estado.collectAsStateWithLifecycle()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsStateWithLifecycle()
    val categoria by vm.categoria.collectAsState()
    val radio by vm.radio.collectAsState()
    val seguimientoActivo by vm.seguimientoActivo.collectAsState()
    val ruta by vm.ruta.collectAsStateWithLifecycle()
    val rutaCargando by vm.rutaCargando.collectAsStateWithLifecycle()
    val errorRuta by vm.errorRuta.collectAsStateWithLifecycle()
    val tienePermisoUbicacion = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) {
            if (iniciarSeguimientoAlDarPermiso) vm.iniciarSeguimiento() else vm.buscar()
        } else {
            vm.permisoDenegado()
        }
        iniciarSeguimientoAlDarPermiso = false
    }

    fun solicitarUbicacion(seguimiento: Boolean = false) {
        if (tienePermisoUbicacion) {
            if (seguimiento) vm.iniciarSeguimiento() else vm.buscar()
        } else {
            iniciarSeguimientoAlDarPermiso = seguimiento
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF091116))) {
        MapaUbicacion(
            estado = estado,
            lugares = lugares,
            rutaPeatonal = ruta,
            seleccionado = seleccionado,
            masCercanoId = masCercano?.id,
            radioMetros = radio,
            mostrarPuntoAzul = tienePermisoUbicacion,
            mapaOscuro = mapaOscuro,
            onSeleccionarLugar = vm::seleccionar,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().height(150.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xCC091116), Color(0x66091116), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = Color(0xFF23413F), modifier = Modifier.size(46.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Explore, contentDescription = null, tint = Color(0xFF65E0CC))
                        }
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("CampusGPS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "TECSUP · SANTA ANITA, LIMA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = { mapaOscuro = !mapaOscuro }) {
                        Icon(
                            if (mapaOscuro) Icons.Rounded.WbSunny else Icons.Rounded.DarkMode,
                            contentDescription = if (mapaOscuro) "Usar mapa claro" else "Usar mapa oscuro",
                            tint = Color.White
                        )
                    }
                }
            }
            LocationStatus(estado = estado, siguiendo = seguimientoActivo)
        }

        DestinationPanel(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            lugares = lugares,
            seleccionado = seleccionado,
            categoria = categoria,
            radio = radio,
            estado = estado,
            seguimientoActivo = seguimientoActivo,
            ruta = ruta,
            rutaCargando = rutaCargando,
            errorRuta = errorRuta,
            onCalcularRuta = vm::calcularRuta,
            onObtenerUbicacion = { solicitarUbicacion() },
            onToggleSeguimiento = {
                if (seguimientoActivo) vm.detenerSeguimiento() else solicitarUbicacion(seguimiento = true)
            },
            onSeleccionar = vm::seleccionar,
            onCategoria = vm::seleccionarCategoria,
            onRadio = vm::seleccionarRadio,
            onMasCercano = { masCercano?.let { vm.seleccionar(it.id) } },
            onRetry = { solicitarUbicacion() },
            onOpenSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
private fun LocationStatus(estado: UbicacionUi, siguiendo: Boolean) {
    val (label, dotColor) = when {
        siguiendo -> "Seguimiento activo · posición en tiempo real" to Color(0xFF65E0CC)
        estado is UbicacionUi.Lista -> "Ubicación lista · elige un destino para ver la conexión" to Color(0xFF65E0CC)
        estado is UbicacionUi.Buscando -> "Buscando tu ubicación…" to Color(0xFFFFD166)
        estado is UbicacionUi.Error -> "Revisa el permiso o el GPS" to Color(0xFFFF8A80)
        else -> "Mapa centrado en el campus TECSUP" to Color(0xFFC4E879)
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xD9142026),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor))
            Text(label, modifier = Modifier.padding(start = 9.dp), style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFE6EEEE), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DestinationPanel(
    modifier: Modifier,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    categoria: String?,
    radio: Int?,
    estado: UbicacionUi,
    seguimientoActivo: Boolean,
    ruta: RutaPeatonal?,
    rutaCargando: Boolean,
    errorRuta: String?,
    onCalcularRuta: () -> Unit,
    onObtenerUbicacion: () -> Unit,
    onToggleSeguimiento: () -> Unit,
    onSeleccionar: (Int) -> Unit,
    onCategoria: (String?) -> Unit,
    onRadio: (Int?) -> Unit,
    onMasCercano: () -> Unit,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var consulta by rememberSaveable { mutableStateOf("") }
    val lugaresVisibles = lugares.filter { item ->
        val texto = consulta.trim()
        texto.isEmpty() || item.lugar.nombre.contains(texto, ignoreCase = true) ||
            item.lugar.categoria.contains(texto, ignoreCase = true)
    }
    val destino = lugares.firstOrNull { it.lugar.id == seleccionado }
    val esSede = seleccionado == 0
    val nombreDestino = destino?.lugar?.nombre ?: if (esSede) "TECSUP · Sede Lima" else "Puntos de interés"
    GlassPanel(modifier = modifier.heightIn(max = 480.dp)) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                Modifier.align(Alignment.CenterHorizontally).width(34.dp).height(4.dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.35f))
            )
            AnimatedVisibility(
                visible = seleccionado != null,
                enter = fadeIn(tween(260)) + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut(tween(180)) + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                seleccionado?.let { lugarId ->
                LugarPhotoCarousel(
                    lugarId = lugarId,
                    nombre = nombreDestino
                )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("EXPLORA EL CAMPUS", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Text(
                        nombreDestino,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        when {
                            estado is UbicacionUi.Lista && destino?.metros != null ->
                                "A ${formatearDistancia(destino.metros)} en línea recta · recorrido peatonal abajo"
                            esSede -> "Campus TECSUP · Santa Anita, Lima"
                            destino != null -> "Destino seleccionado · obtén tu ubicación para ver la conexión"
                            else -> "Selecciona biblioteca, cafetería, comedor u otro punto"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onMasCercano, enabled = lugares.isNotEmpty()) {
                    Icon(Icons.Rounded.NearMe, contentDescription = "Seleccionar el punto más cercano",
                        tint = MaterialTheme.colorScheme.secondary)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onObtenerUbicacion, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Rounded.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(7.dp))
                    Text(if (estado is UbicacionUi.Buscando) "Buscando…" else "Mi ubicación")
                }
                OutlinedButton(
                    onClick = onToggleSeguimiento,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF65E0CC).copy(alpha = 0.55f))
                ) {
                    Icon(
                        if (seguimientoActivo) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (seguimientoActivo) "Detener" else "Seguirme")
                }
            }

            if (estado is UbicacionUi.Buscando) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text("Obteniendo posición…", style = MaterialTheme.typography.labelMedium)
                }
            }
            if (estado is UbicacionUi.Error) {
                MensajeError(error = estado, onRetry = onRetry, onOpenSettings = onOpenSettings)
            }

            if (destino != null || esSede) {
                RouteGuidance(
                    ruta = ruta,
                    cargando = rutaCargando,
                    error = errorRuta,
                    onCalcularRuta = onCalcularRuta
                )
            }

            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Buscar biblioteca, comedor…") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    if (consulta.isNotEmpty()) {
                        IconButton(onClick = { consulta = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                listOf("Todos", "Estudio", "Comida", "Eventos", "Servicios", "Deporte").forEach { label ->
                    val value = label.takeUnless { it == "Todos" }
                    FilterChip(selected = categoria == value, onClick = { onCategoria(value) }, label = { Text(label) })
                }
            }

            if (lugaresVisibles.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    items(lugaresVisibles, key = { it.lugar.id }) { item ->
                        PlaceCard(item = item, selected = seleccionado == item.lugar.id) {
                            onSeleccionar(item.lugar.id)
                        }
                    }
                }
            } else {
                Text(
                    "No encontré lugares con ese nombre.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp))
                Text("Radio", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                listOf(null, 100, 200, 500).forEach { value ->
                    FilterChip(
                        selected = radio == value,
                        onClick = { onRadio(value) },
                        label = { Text(value?.let { "${it} m" } ?: "Todo") }
                    )
                }
            }
            Text(
                "© OpenStreetMap · OpenFreeMap  |  Ubicaciones del campus TECSUP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun RouteGuidance(
    ruta: RutaPeatonal?,
    cargando: Boolean,
    error: String?,
    onCalcularRuta: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.055f),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (ruta?.esReferencial == true) "RUTA INTERNA · ESTIMADA" else "RUTA A PIE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Text(
                        ruta?.let { "${formatearDistancia(it.distanciaMetros.toFloat())} · ${formatearDuracion(it.duracionSegundos)} aprox." }
                            ?: "Recorrido por caminos peatonales",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                OutlinedButton(onClick = onCalcularRuta, enabled = !cargando, shape = RoundedCornerShape(14.dp)) {
                    if (cargando) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(if (ruta == null) "Trazar" else "Actualizar")
                }
            }
            if (error != null) {
                Text(error, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFA3A3))
            }
            ruta?.indicaciones?.forEachIndexed { index, paso ->
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Surface(shape = CircleShape, color = Color(0xFF23413F), modifier = Modifier.size(25.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("${index + 1}", style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF65E0CC), fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        Text(paso.instruccion, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface)
                        if (paso.distanciaMetros > 0) {
                            Text(formatearDistancia(paso.distanciaMetros.toFloat()),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private fun formatearDuracion(segundos: Double): String {
    val minutos = (segundos / 60.0).toInt().coerceAtLeast(1)
    return if (minutos < 60) "$minutos min" else "${minutos / 60} h ${minutos % 60} min"
}

@Composable
private fun LugarPhotoCarousel(lugarId: Int, nombre: String) {
    val fotos = when (lugarId) {
        0 -> listOf(R.drawable.campus_tecsup_1, R.drawable.campus_tecsup_2)
        1 -> listOf(R.drawable.campus_biblioteca_1, R.drawable.campus_biblioteca_2)
        2 -> listOf(R.drawable.campus_cafeteria_1, R.drawable.campus_cafeteria_2)
        3 -> listOf(R.drawable.campus_comedor_1, R.drawable.campus_comedor_2)
        4 -> listOf(R.drawable.campus_auditorio_1, R.drawable.campus_auditorio_2)
        5 -> listOf(R.drawable.campus_estacionamiento_1, R.drawable.campus_estacionamiento_2)
        6 -> listOf(R.drawable.campus_salones_computo_1, R.drawable.campus_salones_computo_2)
        7 -> listOf(R.drawable.campus_cancha_grass_1, R.drawable.campus_cancha_grass_2)
        else -> emptyList()
    }
    if (fotos.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { fotos.size })
    val scope = rememberCoroutineScope()
    LaunchedEffect(lugarId) { pagerState.scrollToPage(0) }
    Surface(
        modifier = Modifier.fillMaxWidth().height(148.dp).shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF182228),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Box {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                Image(
                    painter = painterResource(fotos[page]),
                    contentDescription = "${nombre}, imagen ${page + 1} de ${fotos.size}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(70.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xE6000000))))
            )
            Row(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
                    .padding(start = 14.dp, end = 12.dp, bottom = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(nombre, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    Text("FOTO ${pagerState.currentPage + 1} DE ${fotos.size}",
                        style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.78f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1 + fotos.size) % fotos.size) } },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Imagen anterior",
                            tint = Color.White, modifier = Modifier.size(17.dp))
                    }
                    IconButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1) % fotos.size) } },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Rounded.ArrowForwardIos, contentDescription = "Siguiente imagen",
                            tint = Color.White, modifier = Modifier.size(17.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceCard(item: LugarConDistancia, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.width(168.dp).height(76.dp),
        shape = RoundedCornerShape(17.dp),
        color = if (selected) Color(0xFF214440) else Color.White.copy(alpha = 0.055f),
        border = BorderStroke(1.dp, if (selected) Color(0xFF65E0CC).copy(alpha = 0.65f) else GlassBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.08f), modifier = Modifier.size(34.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null,
                        tint = if (selected) Color(0xFF65E0CC) else Color(0xFFB6C6CA),
                        modifier = Modifier.size(18.dp))
                }
            }
            Column(modifier = Modifier.padding(start = 9.dp).weight(1f)) {
                Text(item.lugar.nombre, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    item.metros?.let(::formatearDistancia) ?: item.lugar.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun GlassPanel(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        color = GlassSurface,
        border = BorderStroke(1.dp, GlassBorder),
        shadowElevation = 16.dp
    ) {
        Column(Modifier.padding(15.dp)) { content() }
    }
}
