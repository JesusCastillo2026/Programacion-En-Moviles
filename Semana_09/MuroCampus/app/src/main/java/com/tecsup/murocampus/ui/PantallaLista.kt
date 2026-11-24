package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun PantallaLista(viewModel: PostsViewModel, onAbrirPost: (Int) -> Unit) {
    val estado by viewModel.lista.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("MuroCampus") }) }) { padding ->
        when (val actual = estado) {
            UiState.Cargando -> Cargando(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorConReintento(actual.mensaje, viewModel::cargar, Modifier.padding(padding))
            is UiState.Exito -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
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
