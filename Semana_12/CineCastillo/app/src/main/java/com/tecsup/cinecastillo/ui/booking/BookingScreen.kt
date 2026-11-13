package com.tecsup.cinecastillo.ui.booking

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.cinecastillo.domain.model.Movie
import com.tecsup.cinecastillo.domain.model.CinemaReservation
import com.tecsup.cinecastillo.ui.lista.MovieListUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class DayOption(val calendar: Calendar, val label: String, val dateLabel: String, val past: Boolean)

@Composable
fun BookingScreen(
    state: MovieListUiState,
    initialMovieId: Int? = null,
    initialVenue: String = "",
    initialDayOffset: Int = 0,
    initialTime: String = "",
    bookingState: BookingUiState = BookingUiState(),
    onConfirmReservation: (CinemaReservation) -> Unit = {},
    onResetReservation: () -> Unit = {},
    onRetryCatalog: () -> Unit = {}
) {
    var step by rememberSaveable { mutableIntStateOf(if (initialMovieId != null && initialVenue.isNotBlank()) 2 else 0) }
    var venue by rememberSaveable { mutableStateOf(initialVenue) }
    var movieId by rememberSaveable { mutableIntStateOf(initialMovieId ?: -1) }
    var ticketCount by rememberSaveable { mutableIntStateOf(1) }
    var selectedSeats by rememberSaveable { mutableStateOf(setOf<String>()) }
    var format by rememberSaveable { mutableStateOf("") }
    var selectedDayOffset by rememberSaveable { mutableIntStateOf(initialDayOffset.coerceIn(0, 6)) }
    var selectedTime by rememberSaveable { mutableStateOf(initialTime) }
    val listState = rememberLazyListState()
    LaunchedEffect(step) { listState.animateScrollToItem(0) }

    val movie = state.movies.firstOrNull { it.id == movieId }
    val venueModel = cineCastilloVenues.firstOrNull { it.name == venue }
    val compatibleFormats = compatibleFormats(movie, venueModel)
    val dateChoices = rememberDayOptions()
    val displayedDay = dateChoices.getOrNull((selectedDayOffset + 2).coerceIn(0, dateChoices.lastIndex))
    val times = displayedDay?.let { timesFor(it.calendar) }.orEmpty()
    val occupiedSeats = occupiedSeatsFor("$movieId|$venue|$selectedDayOffset|$selectedTime")
    LaunchedEffect(bookingState.savedReservation) {
        if (bookingState.savedReservation != null) step = 6
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Reserva tus entradas", style = MaterialTheme.typography.headlineMedium)
                Text("Selecciona sede, película y función.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (step == 6) "Reserva completada" else "Paso ${step.coerceAtMost(5) + 1} de 6", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        when (step) {
            0 -> {
                item { Text("1. Elige una sede", style = MaterialTheme.typography.titleLarge) }
                item { SedesScreen(venue) { selected -> venue = selected.name; format = ""; step = 1 } }
            }
            1 -> {
                item { Text("2. Elige una película", style = MaterialTheme.typography.titleLarge) }
                item { Text("Funciones disponibles en $venue", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (state.loading && state.movies.isEmpty()) item { Text("Cargando cartelera…") }
                if (state.error != null) item { Text(state.error, color = MaterialTheme.colorScheme.error) }
                if (!state.loading && state.error != null && state.movies.isEmpty()) item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("No se pudo cargar la cartelera. Revisa tu conexión y vuelve a intentar.", color = MaterialTheme.colorScheme.error)
                        OutlinedButton(onClick = onRetryCatalog) { Text("Reintentar") }
                    }
                }
                if (!state.loading && state.movies.isEmpty() && state.error == null) item {
                    Text("No hay películas disponibles en este momento. Actualiza el catálogo e inténtalo de nuevo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(state.visible.ifEmpty { state.movies }.size) { index ->
                    val choice = state.visible.ifEmpty { state.movies }[index]
                    MovieChoice(choice, choice.id == movieId) {
                        movieId = choice.id
                        selectedSeats = emptySet()
                        format = ""
                        step = 2
                    }
                }
                item { Text("Toca una película para continuar con la cantidad de entradas y los asientos.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            2 -> {
                item { Text("3. Fecha y horario", style = MaterialTheme.typography.titleLarge) }
                item { Text("${movie?.title.orEmpty()} · $venue", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item { Text("Las fechas pasadas se deshabilitan y los horarios respetan el horario de la sede.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        dateChoices.forEachIndexed { index, option ->
                            val isSelected = selectedDayOffset == index - 2
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!option.past) {
                                        selectedDayOffset = index - 2
                                        selectedTime = ""
                                        selectedSeats = emptySet()
                                    }
                                },
                                enabled = !option.past,
                                label = { Text("${if (option.past) "× " else ""}${option.label} · ${option.dateLabel}") }
                            )
                        }
                    }
                }
                item {
                    Text("Horarios disponibles", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        times.forEach { time ->
                            FilterChip(selected = selectedTime == time, onClick = {
                                if (selectedTime != time) selectedSeats = emptySet()
                                selectedTime = time
                            }, label = { Text(time) })
                        }
                    }
                    if (times.isEmpty()) Text("No quedan funciones para esta fecha.", color = MaterialTheme.colorScheme.error)
                }
                item { StepButtons(onBack = { step = 1 }, onNext = { step = 3 }, enabled = selectedTime in times) }
            }
            3 -> {
                item { Text("4. Cantidad y asientos", style = MaterialTheme.typography.titleLarge) }
                item { Text("${movie?.title.orEmpty()} · ${displayedDay?.dateLabel.orEmpty()} · $selectedTime", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("¿Cuántas entradas? Máximo 5", style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (1..5).forEach { amount ->
                                FilterChip(selected = ticketCount == amount, onClick = { ticketCount = amount; selectedSeats = emptySet() }, label = { Text(amount.toString()) })
                            }
                        }
                    }
                }
                item { SeatPicker(selectedSeats, ticketCount, occupiedSeats, onChange = { selectedSeats = it }) }
                item { Text("Selecciona exactamente $ticketCount asiento${if (ticketCount == 1) "" else "s"}.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item { StepButtons(onBack = { step = 2 }, onNext = { step = 4 }, enabled = selectedSeats.size == ticketCount) }
            }
            4 -> {
                item { Text("5. Formato de sala", style = MaterialTheme.typography.titleLarge) }
                item { Text("Formatos compatibles con esta película y sede", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(compatibleFormats.size) { index ->
                    val option = compatibleFormats[index]
                    FormatChoice(option, ticketPrice(option), format == option) { format = option }
                }
                item { Text("La compatibilidad es información de demostración.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item { StepButtons(onBack = { step = 3 }, onNext = { step = 5 }, enabled = format in compatibleFormats) }
            }
            5 -> {
                item { Text("6. Revisa y confirma", style = MaterialTheme.typography.titleLarge) }
                item {
                    val amount = ticketCount * ticketPrice(format)
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh), shape = RoundedCornerShape(22.dp)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(movie?.title.orEmpty(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Sede · $venue")
                            Text("Formato · $format")
                            Text("Precio por entrada · S/ ${"%.2f".format(Locale.US, ticketPrice(format))}")
                            Text("Cantidad · $ticketCount ${if (ticketCount == 1) "entrada" else "entradas"}")
                            Text("Función · ${displayedDay?.dateLabel.orEmpty()} · $selectedTime")
                            Text("Asientos · ${selectedSeats.sorted().joinToString()}")
                            Text("Total estimado · S/ ${"%.2f".format(Locale.US, amount)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item { Text("Reserva de demostración: no procesa pagos ni bloquea butacas en una boletería real.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (bookingState.error != null) item { Text(bookingState.error, color = MaterialTheme.colorScheme.error) }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { step = 4 }, modifier = Modifier.weight(1f), enabled = !bookingState.saving) { Text("Atrás") }
                        Button(
                            onClick = {
                                val reservationCode = "CC-${System.currentTimeMillis()}"
                                onConfirmReservation(CinemaReservation(
                                    id = reservationCode,
                                    movieTitle = movie?.title.orEmpty(),
                                    venue = venue,
                                    format = format,
                                    date = displayedDay?.dateLabel.orEmpty(),
                                    time = selectedTime,
                                    seats = selectedSeats.sorted(),
                                    total = ticketCount * ticketPrice(format)
                                ))
                            },
                            enabled = !bookingState.saving && movie != null && selectedSeats.size == ticketCount && selectedTime.isNotBlank() && format.isNotBlank(),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (bookingState.saving) androidx.compose.material3.CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            else Text("Guardar reserva")
                        }
                    }
                }
            }
            else -> {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh), shape = RoundedCornerShape(24.dp)) {
                        Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("✓", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                            Text("¡Tu reserva está lista!", style = MaterialTheme.typography.headlineSmall)
                            Text("${movie?.title.orEmpty()} · $format", style = MaterialTheme.typography.titleMedium)
                            Text("${displayedDay?.dateLabel.orEmpty()} · $selectedTime")
                            Text("Asientos: ${selectedSeats.sorted().joinToString()}")
                            Text("Código ${bookingState.savedReservation?.id.orEmpty()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item { Button(onClick = {
                    onResetReservation()
                    step = 0
                    venue = ""
                    movieId = -1
                    selectedSeats = emptySet()
                    selectedTime = ""
                    format = ""
                    ticketCount = 1
                }, modifier = Modifier.fillMaxWidth()) { Text("Reservar otra función") } }
            }
        }
    }
}

@Composable
private fun rememberDayOptions(): List<DayOption> {
    val now = Calendar.getInstance()
    val today = (now.clone() as Calendar).apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
    val formatDay = SimpleDateFormat("EEE", Locale("es", "PE"))
    val formatDate = SimpleDateFormat("d MMM yyyy", Locale("es", "PE"))
    return (-2..6).map { offset ->
        val day = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) }
        val label = when (offset) { -2 -> "Día anterior"; -1 -> "Ayer"; 0 -> "Hoy"; 1 -> "Mañana"; else -> formatDay.format(day.time).replaceFirstChar(Char::uppercase) }
        DayOption(day, label, formatDate.format(day.time), offset < 0)
    }
}

internal fun timesFor(day: Calendar): List<String> {
    val now = Calendar.getInstance()
    val sunday = day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    val slots = if (sunday) listOf("10:00", "11:00", "12:00", "13:00") else listOf("10:00", "12:00", "14:00", "16:00", "18:00")
    val sameDay = day.get(Calendar.YEAR) == now.get(Calendar.YEAR) && day.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
    return slots.filter { time ->
        if (!sameDay) true else {
            val parts = time.split(":").map(String::toInt)
            parts[0] > now.get(Calendar.HOUR_OF_DAY) || parts[0] == now.get(Calendar.HOUR_OF_DAY) && parts[1] > now.get(Calendar.MINUTE)
        }
    }
}

fun showtimesForOffset(offset: Int): List<String> {
    val day = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, offset)
    }
    return timesFor(day)
}

internal fun compatibleFormats(movie: Movie?, venue: CinemaVenue?): List<String> {
    if (movie == null || venue == null) return emptyList()
    val venueFormats = venue.services.filter { it in listOf("2D", "3D", "4DX", "IMAX", "VIP") }
    val movieFormats = when (kotlin.math.abs(movie.id) % 3) {
        0 -> listOf("2D", "3D", "IMAX")
        1 -> listOf("2D", "4DX", "VIP")
        else -> listOf("2D", "3D", "4DX", "IMAX", "VIP")
    }
    return movieFormats.filter { it in venueFormats }.ifEmpty { venueFormats.take(1) }
}

private fun ticketPrice(format: String): Double = when (format) {
    "4DX" -> 35.00
    "IMAX" -> 32.00
    "VIP" -> 30.00
    "3D" -> 25.00
    else -> 18.00
}

@Composable
private fun FormatChoice(format: String, price: Double, selected: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(format, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(formatDescription(format), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("S/ ${"%.2f".format(Locale.US, price)} por entrada", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Text("›", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun formatDescription(format: String) = when (format) {
    "4DX" -> "Movimiento y efectos ambientales"
    "IMAX" -> "Pantalla de gran formato y sonido envolvente"
    "VIP" -> "Asientos premium y mayor comodidad"
    "3D" -> "Imagen con profundidad"
    else -> "Proyección digital clásica"
}

@Composable
private fun occupiedSeatsFor(sessionKey: String): Set<String> {
    val seed = sessionKey.hashCode()
    return buildSet {
        listOf("A1", "A2", "A3", "A4", "A5", "A6", "B1", "B2", "B3", "B4", "B5", "B6", "C1", "C2", "C3", "C4", "C5", "C6", "D1", "D2", "D3", "D4", "D5", "D6", "E1", "E2", "E3", "E4", "E5", "E6")
            .forEachIndexed { index, seat ->
                if (Math.floorMod(seed + index * 17, 11) < 2) add(seat)
            }
    }
}

@Composable
private fun SeatPicker(selectedSeats: Set<String>, requiredCount: Int, occupiedSeats: Set<String>, onChange: (Set<String>) -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(Modifier.fillMaxWidth(0.84f), color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(50)) {
            Text("PANTALLA", Modifier.padding(vertical = 12.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            listOf("A", "B", "C", "D", "E").forEachIndexed { rowIndex, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(row, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    (1..6).forEach { number ->
                        val seat = "$row$number"
                        val occupied = seat in occupiedSeats
                        SeatTile(seat, occupied, seat in selectedSeats, selectedSeats.size < requiredCount || seat in selectedSeats) {
                            if (occupied) return@SeatTile
                            onChange(if (seat in selectedSeats) selectedSeats - seat else selectedSeats + seat)
                        }
                    }
                    Text(row, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("● Libre", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("● Ocupado", color = MaterialTheme.colorScheme.error)
            Text("● Elegido", color = MaterialTheme.colorScheme.primary)
        }
        Text("${selectedSeats.size} de $requiredCount seleccionados", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun SeatTile(label: String, occupied: Boolean, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val color by animateColorAsState(
        targetValue = when { occupied -> MaterialTheme.colorScheme.error.copy(alpha = 0.55f); selected -> MaterialTheme.colorScheme.primary; else -> MaterialTheme.colorScheme.surfaceContainerHigh },
        label = "seatColor"
    )
    val scale by animateFloatAsState(if (selected) 1.08f else 1.00f, label = "seatScale")
    Surface(
        modifier = Modifier.size(36.dp).scale(scale)
            .semantics {
                contentDescription = "Asiento $label"
                stateDescription = when {
                    occupied -> "Ocupado"
                    selected -> "Seleccionado"
                    else -> "Disponible"
                }
                if (occupied || !enabled) disabled()
            }
            .then(if (!occupied && enabled) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 5.dp, bottomEnd = 5.dp), color = color
    ) { Box(contentAlignment = Alignment.Center) { Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) } }
}

@Composable
private fun StepButtons(onBack: () -> Unit, onNext: () -> Unit, enabled: Boolean, nextLabel: String = "Continuar") {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Atrás") }
        Button(onClick = onNext, enabled = enabled, modifier = Modifier.weight(1f)) { Text(nextLabel) }
    }
}

@Composable
private fun MovieChoice(movie: Movie, selected: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("★ %.1f · ${movie.releaseDate.take(4)}".format(Locale.US, movie.rating), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(if (selected) "✓" else "○", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
