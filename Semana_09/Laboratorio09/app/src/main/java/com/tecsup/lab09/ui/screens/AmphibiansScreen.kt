package com.tecsup.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.lab09.ui.components.ErrorState
import com.tecsup.lab09.ui.components.LoadingState
import com.tecsup.lab09.viewmodel.PostViewModel

@Composable
fun AmphibiansScreen(viewModel: PostViewModel) {
    val state by viewModel.amphibians.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text("Vida anfibia", Modifier.padding(top = 16.dp), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Un catálogo visual consultado desde una segunda API REST.", Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        when {
            state.isLoading -> LoadingState("Cargando especies…")
            state.error != null -> ErrorState(state.error.orEmpty(), viewModel::loadAmphibians)
            else -> LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.animals, key = { it.name }) { animal ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = animal.imageUrl,
                                contentDescription = "Imagen de ${animal.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(14.dp))
                            )
                            Text(animal.name, Modifier.padding(top = 12.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(animal.type.uppercase(), Modifier.padding(top = 3.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(animal.description, Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
