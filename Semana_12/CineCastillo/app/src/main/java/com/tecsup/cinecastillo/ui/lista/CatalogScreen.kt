package com.tecsup.cinecastillo.ui.lista

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tecsup.cinecastillo.domain.model.Movie

@Composable
fun CatalogScreen(
 state: MovieListUiState,onSearch:(String)->Unit,onSort:(MovieSort)->Unit,
 onFavorites:(Boolean)->Unit,onSync:()->Unit,onDetail:(Int)->Unit,
 onFavorite:(Int,Boolean)->Unit,soloFavoritos:Boolean=false
){
 LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(horizontal=20.dp,vertical=18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
  item {
   Text(if(soloFavoritos) "Tu colección" else "Descubre",style=MaterialTheme.typography.headlineLarge)
   Text(if(soloFavoritos) "Historias que quieres volver a ver" else "Una historia para cada momento",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
   Spacer(Modifier.height(18.dp))
   if(!soloFavoritos && state.movies.isNotEmpty()) FeaturedMovie(state.movies.first(),onDetail)
   if(!soloFavoritos){
    Spacer(Modifier.height(18.dp))
    OutlinedTextField(state.query,onSearch,modifier=Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(18.dp),label={Text("Buscar películas")})
    Spacer(Modifier.height(8.dp))
    LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
     item { FilterChip(state.sort==MovieSort.TITLE,{onSort(MovieSort.TITLE)},label={Text("A-Z")}) }
     item { FilterChip(state.sort==MovieSort.RATING,{onSort(MovieSort.RATING)},label={Text("Mejor valoradas")}) }
     item { FilterChip(state.sort==MovieSort.NEWEST,{onSort(MovieSort.NEWEST)},label={Text("Recientes")}) }
    }
    Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()){
     TextButton(onClick={onFavorites(false)}){Text("Todas")}
     TextButton(onClick={onFavorites(true)}){Text("Favoritas")}
     Spacer(Modifier.weight(1f))
     TextButton(onClick=onSync){Text("Actualizar")}
    }
   }
   if(state.offline) Text("Sin conexión · mostrando películas guardadas",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.secondary)
   if(state.loading && state.movies.isEmpty()) CircularProgressIndicator()
   if(state.error!=null) { Text(state.error,color=MaterialTheme.colorScheme.error);Button(onClick=onSync){Text("Reintentar")} }
   if(!state.loading && state.error==null && state.visible.isEmpty()) EmptyCollection(if(soloFavoritos) "Aún no tienes favoritas" else if(state.movies.isEmpty()) "No hay películas guardadas" else "No encontramos resultados")
   if(state.visible.isNotEmpty()) Text(if(soloFavoritos) "Favoritas" else "Películas para ti",style=MaterialTheme.typography.titleLarge,modifier=Modifier.padding(top=12.dp))
  }
  items(state.visible,key={it.id}) { movie -> MovieCard(movie,{onDetail(movie.id)},{onFavorite(movie.id,!movie.favorite)}) }
 }
}
@Composable private fun FeaturedMovie(movie:Movie,onDetail:(Int)->Unit){
 Card(Modifier.fillMaxWidth().clickable { onDetail(movie.id) },shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainerHigh)){
  Row(Modifier.fillMaxWidth().height(172.dp),verticalAlignment=Alignment.CenterVertically){
   AsyncImage(movie.posterUrl,"Póster de ${movie.title}",Modifier.width(112.dp).fillMaxHeight().clip(RoundedCornerShape(topStart=26.dp,bottomStart=26.dp)),contentScale=ContentScale.Crop)
   Column(Modifier.weight(1f).padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
    Text("DESTACADA",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.secondary)
    Text(movie.title,style=MaterialTheme.typography.titleLarge,maxLines=3,overflow=TextOverflow.Ellipsis)
    Text("${movie.releaseDate.take(4)}  ·  ★ ${"%.1f".format(movie.rating)}",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    Text("Ver detalle  →",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
   }
  }
 }
}
@Composable private fun MovieCard(movie:Movie,onDetail:()->Unit,onFavorite:()->Unit){
 Card(Modifier.fillMaxWidth().clickable(onClick=onDetail),shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceContainer)){
  Row(Modifier.padding(10.dp),verticalAlignment=Alignment.CenterVertically){
   AsyncImage(movie.posterUrl,"Póster de ${movie.title}",Modifier.size(78.dp,110.dp).clip(RoundedCornerShape(12.dp)),contentScale=ContentScale.Crop)
   Column(Modifier.weight(1f).padding(horizontal=14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
    Text(movie.title,style=MaterialTheme.typography.titleMedium,maxLines=2,overflow=TextOverflow.Ellipsis)
    Text(movie.releaseDate.take(4),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
    Text("★ ${"%.1f".format(movie.rating)}",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.secondary)
   }
   IconButton(onClick=onFavorite){Icon(if(movie.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,if(movie.favorite) "Quitar de favoritos" else "Guardar en favoritos",tint=MaterialTheme.colorScheme.primary)}
  }
 }
}
@Composable private fun EmptyCollection(message:String){
 Surface(Modifier.fillMaxWidth().padding(top=22.dp),shape=RoundedCornerShape(22.dp),color=MaterialTheme.colorScheme.surfaceContainer){
  Column(Modifier.padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("✦",style=MaterialTheme.typography.headlineLarge,color=MaterialTheme.colorScheme.primary);Text(message,style=MaterialTheme.typography.titleMedium);Text("Explora el catálogo y vuelve cuando quieras.",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)}
 }
}
