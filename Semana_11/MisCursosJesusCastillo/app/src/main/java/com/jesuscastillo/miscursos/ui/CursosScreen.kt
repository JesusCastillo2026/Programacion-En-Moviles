package com.jesuscastillo.miscursos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jesuscastillo.miscursos.data.Curso
import com.jesuscastillo.miscursos.data.RepositorioCursos

@Composable
fun CursosScreen(user: FirebaseUser, onCerrarSesion: () -> Unit) {
    val repositorio = remember { RepositorioCursos(FirebaseFirestore.getInstance()) }
    var cursos by remember { mutableStateOf(emptyList<Curso>()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(user.uid, repositorio) {
        val listener = repositorio.observarCursos(
            uid = user.uid,
            alCambiar = { lista ->
                cursos = lista
                cargando = false
                error = null
            },
            alFallar = { exception ->
                cargando = false
                error = exception.localizedMessage ?: "No se pudieron cargar tus cursos."
            }
        )
        onDispose { listener.remove() }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text("Mis Cursos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Bienvenido, ${user.email.orEmpty()}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
            Spacer(Modifier.height(20.dp))

            when {
                cargando -> Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }

                error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)

                cursos.isEmpty() -> Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Aún no tienes cursos registrados.", style = MaterialTheme.typography.titleMedium)
                    Text("Cuando agregues uno, aparecerá aquí.")
                }

                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cursos, key = { it.id }) { curso ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(curso.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                                Text("Docente: ${curso.docente.ifBlank { "Sin especificar" }}")
                                Text("Créditos: ${curso.creditos}")
                                if (curso.descripcion.isNotBlank()) Text(curso.descripcion)
                            }
                        }
                    }
                }
            }
        }
    }
}
