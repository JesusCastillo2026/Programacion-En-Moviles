package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrear(viewModel: PostsViewModel, onVolver: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    val resultado by viewModel.resultadoCrear.collectAsStateWithLifecycle()
    LaunchedEffect(resultado) {
        if (resultado is UiState.Exito) {
            titulo = ""
            contenido = ""
        }
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("MuroCampus") },
            navigationIcon = { TextButton(onClick = onVolver) { Text("‹ Atrás", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary) } },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            )
        )
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                minLines = 4
            )
            val enviando = resultado is UiState.Cargando
            Button(
                onClick = { viewModel.publicar(titulo, contenido) },
                enabled = titulo.isNotBlank() && contenido.isNotBlank() && !enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (enviando) CircularProgressIndicator() else Text("Publicar")
            }
            when (val actual = resultado) {
                is UiState.Exito -> Text("Publicado con id ${actual.datos.id}")
                is UiState.Error -> Text(actual.mensaje, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                else -> Unit
            }
        }
    }
}
