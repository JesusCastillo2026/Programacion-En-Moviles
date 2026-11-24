package com.tecsup.murocampus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(viewModel: PostsViewModel, onVolver: () -> Unit) {
    BackHandler(onBack = onVolver)
    val estado by viewModel.detalle.collectAsStateWithLifecycle()
    val resultadoBorrar by viewModel.resultadoBorrar.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("MuroCampus") },
            navigationIcon = { TextButton(onClick = onVolver) { Text("‹ Atrás", color = MaterialTheme.colorScheme.onPrimary) } },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }) { padding ->
        when (val actual = estado) {
            UiState.Cargando -> Cargando("Cargando publicación…", Modifier.padding(padding))
            is UiState.Error -> ErrorConReintento(actual.mensaje, viewModel::cargarDetalle, Modifier.padding(padding))
            is UiState.Exito -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    TarjetaPost(actual.datos.post, onClick = {})
                    OutlinedButton(
                        onClick = { viewModel.borrar(actual.datos.post.id) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) { Text("Eliminar publicación (demo)") }
                    when (val borrado = resultadoBorrar) {
                        UiState.Cargando -> Text("Enviando solicitud…", Modifier.padding(top = 4.dp))
                        is UiState.Exito -> Text(
                            "Solicitud DELETE aceptada. JSONPlaceholder no conserva cambios.",
                            Modifier.padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        is UiState.Error -> Text(
                            borrado.mensaje,
                            Modifier.padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                        null -> Unit
                    }
                    Text("Comentarios (${actual.datos.comentarios.size})", Modifier.padding(top = 12.dp, bottom = 4.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                items(actual.datos.comentarios, key = { it.id }) { comentario ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFEEF2F7))) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(comentario.email, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text(comentario.body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
