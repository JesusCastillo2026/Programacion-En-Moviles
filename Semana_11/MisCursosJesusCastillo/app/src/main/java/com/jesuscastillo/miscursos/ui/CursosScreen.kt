package com.jesuscastillo.miscursos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jesuscastillo.miscursos.data.Curso
import com.jesuscastillo.miscursos.data.RepositorioCursos
import kotlinx.coroutines.launch

@Composable
fun CursosScreen(user: FirebaseUser, onCerrarSesion: () -> Unit) {
    val repositorio = remember { RepositorioCursos(FirebaseFirestore.getInstance()) }
    var cursos by remember { mutableStateOf(emptyList<Curso>()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var cursoEnEdicion by remember { mutableStateOf<Curso?>(null) }
    var cursoParaEliminar by remember { mutableStateOf<Curso?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                cursoEnEdicion = null
                mostrarFormulario = true
            }) { Text("+") }
        }
    ) { padding ->
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
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = {
                                        cursoEnEdicion = curso
                                        mostrarFormulario = true
                                    }) { Text("Editar") }
                                    TextButton(onClick = { cursoParaEliminar = curso }) { Text("Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        CursoFormDialog(
            curso = cursoEnEdicion,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { curso ->
                repositorio.guardarCurso(user.uid, curso) { exception ->
                    scope.launch {
                        if (exception == null) {
                            snackbar.showSnackbar(if (curso.id.isBlank()) "Curso creado." else "Curso actualizado.")
                            mostrarFormulario = false
                        } else {
                            snackbar.showSnackbar(exception.localizedMessage ?: "No se pudo guardar el curso.")
                        }
                    }
                }
            }
        )
    }

    cursoParaEliminar?.let { curso ->
        AlertDialog(
            onDismissRequest = { cursoParaEliminar = null },
            title = { Text("Eliminar curso") },
            text = { Text("¿Deseas eliminar ${curso.titulo} de tu lista?") },
            confirmButton = {
                TextButton(onClick = {
                    repositorio.eliminarCurso(user.uid, curso.id) { exception ->
                        scope.launch {
                            snackbar.showSnackbar(
                                if (exception == null) "Curso eliminado."
                                else exception.localizedMessage ?: "No se pudo eliminar el curso."
                            )
                            cursoParaEliminar = null
                        }
                    }
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { cursoParaEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CursoFormDialog(
    curso: Curso?,
    onDismiss: () -> Unit,
    onGuardar: (Curso) -> Unit
) {
    var titulo by remember(curso?.id) { mutableStateOf(curso?.titulo.orEmpty()) }
    var docente by remember(curso?.id) { mutableStateOf(curso?.docente.orEmpty()) }
    var creditos by remember(curso?.id) { mutableStateOf(curso?.creditos?.toString().orEmpty()) }
    var descripcion by remember(curso?.id) { mutableStateOf(curso?.descripcion.orEmpty()) }
    val creditosValidados = creditos.toIntOrNull()?.takeIf { it > 0 }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (curso == null) "Agregar curso" else "Editar curso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Nombre del curso") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = docente,
                    onValueChange = { docente = it },
                    label = { Text("Docente") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = creditos,
                    onValueChange = { creditos = it.filter(Char::isDigit) },
                    label = { Text("Créditos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = titulo.isNotBlank() && creditosValidados != null,
                onClick = {
                    onGuardar(
                        Curso(
                            id = curso?.id.orEmpty(),
                            titulo = titulo.trim(),
                            docente = docente.trim(),
                            creditos = creditosValidados ?: 0,
                            descripcion = descripcion.trim()
                        )
                    )
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
