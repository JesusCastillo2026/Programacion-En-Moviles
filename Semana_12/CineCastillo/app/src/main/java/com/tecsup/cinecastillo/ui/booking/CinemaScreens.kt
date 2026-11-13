package com.tecsup.cinecastillo.ui.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.tecsup.cinecastillo.domain.model.Movie
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CinemaVenue(
    val name: String,
    val district: String,
    val address: String,
    val services: List<String>
)

val cineCastilloVenues = listOf(
    CinemaVenue("Cine Castillo San Isidro", "San Isidro", "Av. Rivera Navarrete 450", listOf("IMAX", "3D", "Dulcería")),
    CinemaVenue("Cine Castillo Miraflores", "Miraflores", "Av. Larco 820", listOf("2D", "VIP", "Dulcería")),
    CinemaVenue("Cine Castillo SJL", "San Juan de Lurigancho", "Av. Próceres 1630", listOf("2D", "3D", "4DX"))
)

@Composable
fun SedesScreen(
    selectedVenue: String?,
    onVenueSelected: (CinemaVenue) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Elige tu cine", style = MaterialTheme.typography.titleLarge)
        Text("Sedes de demostración", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        cineCastilloVenues.forEach { venue ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onVenueSelected(venue) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedVenue == venue.name) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(venue.name, style = MaterialTheme.typography.titleMedium)
                        Text(venue.address, style = MaterialTheme.typography.bodyMedium)
                        Text(venue.services.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    RadioButton(selected = selectedVenue == venue.name, onClick = { onVenueSelected(venue) })
                }
            }
        }
        Text("Las direcciones y servicios son referenciales para esta demostración.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CarteleraScreen(
    movies: List<Movie>,
    onReserve: (Movie, CinemaVenue, Int, String) -> Unit,
    onMovieDetails: (Int) -> Unit,
    loading: Boolean = false,
    error: String? = null,
    offline: Boolean = false,
    onRetry: () -> Unit = {}
) {
    var selectedDayOffset by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    var venueName by rememberSaveable { mutableStateOf(cineCastilloVenues.first().name) }
    val days = remember {
        val dayFormat = SimpleDateFormat("EEE d", Locale("es", "PE"))
        (0..3).map { offset ->
            val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
            val label = when (offset) {
                0 -> "Hoy"
                1 -> "Mañana"
                else -> dayFormat.format(calendar.time).replaceFirstChar(Char::uppercase)
            }
            offset to label
        }
    }
    val day = days.first { it.first == selectedDayOffset }.second
    val venue = cineCastilloVenues.first { it.name == venueName }
    val times = showtimesForOffset(selectedDayOffset)
    var selectedTime by rememberSaveable(venueName, selectedDayOffset) { mutableStateOf(times.firstOrNull().orEmpty()) }
    val premieres = movies.sortedByDescending { it.releaseDate }.take(3)
    val pagerState = rememberPagerState(pageCount = { premieres.size })
    LaunchedEffect(premieres.size) {
        if (premieres.size > 1) while (true) {
            delay(4200)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % premieres.size)
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Cartelera", style = MaterialTheme.typography.headlineMedium)
                Text("Filtra por sede y fecha. Toca una película para reservar esa función.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Funciones de demostración · $day", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                if (offline) Text("Estás viendo el catálogo guardado en este dispositivo. Las funciones son de muestra.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                if (error != null && movies.isEmpty()) Text("No se pudo consultar la cartelera. Revisa tu conexión y vuelve a intentar.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(days) { (offset, label) ->
                        FilterChip(selected = selectedDayOffset == offset, onClick = { selectedDayOffset = offset }, label = { Text(label) })
                    }
                }
                Text("Sede", style = MaterialTheme.typography.titleMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(cineCastilloVenues) { option ->
                        FilterChip(selected = venueName == option.name, onClick = { venueName = option.name }, label = { Text(option.district) })
                    }
                }
                if (premieres.isNotEmpty()) {
                    Text("Próximos estrenos", style = MaterialTheme.typography.titleLarge)
                    HorizontalPager(
                        state = pagerState,
                        pageSize = PageSize.Fixed(224.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 42.dp),
                        pageSpacing = 12.dp,
                        modifier = Modifier.fillMaxWidth().height(310.dp)
                    ) { page ->
                        val movie = premieres[page]
                        val distance = kotlin.math.abs(pagerState.getOffsetDistanceInPages(page)).coerceIn(0.00f, 1.00f)
                        PremierePoster(
                            movie = movie,
                            modifier = Modifier.graphicsLayer {
                                val scale = 1.00f - distance * 0.16f
                                scaleX = scale
                                scaleY = scale
                                alpha = 1.00f - distance * 0.28f
                            },
                            onClick = { onMovieDetails(movie.id) }
                        )
                    }
                }
            }
        }
        if (loading && movies.isEmpty()) item { androidx.compose.material3.CircularProgressIndicator() }
        if (!loading && movies.isEmpty()) item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (error != null) "No se pudo consultar la cartelera. Revisa tu conexión."
                    else "No hay películas para mostrar en este momento.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                androidx.compose.material3.OutlinedButton(onClick = onRetry) { Text("Actualizar cartelera") }
            }
        }
        items(movies, key = { it.id }) { movie ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = "Póster de ${movie.title}",
                        modifier = Modifier.size(width = 96.dp, height = 140.dp).clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(movie.title, modifier = Modifier.clickable { onMovieDetails(movie.id) }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("★ %.1f  ·  ${movie.releaseDate.take(4)}".format(movie.rating), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(compatibleFormats(movie, venue).joinToString("   "), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text("Horarios disponibles", style = MaterialTheme.typography.labelLarge)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(times) { time ->
                                FilterChip(selected = selectedTime == time, onClick = { selectedTime = time }, label = { Text(time) })
                            }
                        }
                        if (times.isEmpty()) Text("No quedan funciones en esta fecha.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        androidx.compose.material3.Button(
                            onClick = { onReserve(movie, venue, selectedDayOffset, selectedTime) },
                            enabled = selectedTime in times,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (selectedTime in times) "Reservar · $selectedTime" else "Sin funciones disponibles") }
                    }
                }
            }
        }
        item { Text("Sedes, formatos y horarios son datos de muestra; la disponibilidad real puede variar.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun PremierePoster(movie: Movie, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = "Ver detalle de ${movie.title}",
                modifier = Modifier.fillMaxWidth().height(310.dp).clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp).align(androidx.compose.ui.Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color.Transparent, androidx.compose.ui.graphics.Color(0xFF0D1018))))
            )
            Column(
                modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("EN CARTELERA", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text(movie.title, style = MaterialTheme.typography.titleLarge, maxLines = 2, color = androidx.compose.ui.graphics.Color.White)
                Text("★ %.1f".format(movie.rating), style = MaterialTheme.typography.bodyMedium, color = androidx.compose.ui.graphics.Color.White)
            }
        }
    }
}
