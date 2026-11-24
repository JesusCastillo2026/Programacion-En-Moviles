package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(viewModel: PostsViewModel, onAbrirPost: (Int) -> Unit, onCrear: () -> Unit) {
    val estado by viewModel.lista.collectAsStateWithLifecycle()
    val filtro by viewModel.usuarioFiltro.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopAppBar(title = { Text("MuroCampus") }) },
        floatingActionButton = { FloatingActionButton(onClick = onCrear) { Text("+") } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(selected = filtro == null, onClick = { viewModel.filtrar(null) }, label = { Text("Todos") })
                }
                items(10) { index ->
                    val userId = index + 1
                    FilterChip(selected = filtro == userId, onClick = { viewModel.filtrar(userId) }, label = { Text("Usuario $userId") })
                }
            }
            when (val actual = estado) {
                UiState.Cargando -> Cargando(modifier = Modifier.weight(1f))
                is UiState.Error -> ErrorConReintento(actual.mensaje, viewModel::cargar)
                is UiState.Exito -> if (actual.datos.isEmpty()) {
                    Text("Sin publicaciones", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(actual.datos, key = { it.id }) { post ->
                            TarjetaPost(post, onClick = { onAbrirPost(post.id) })
                        }
                    }
                }
            }
        }
    }
}
