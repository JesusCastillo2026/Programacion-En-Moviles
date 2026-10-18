package com.tecsup.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.gymtracker.data.Repositorio
import com.tecsup.gymtracker.data.Rutina
import com.tecsup.gymtracker.data.Usuario
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repositorio.inicializar(applicationContext)
        setContent { MaterialTheme { GymTrackerApp() } }
    }
}

@Composable
fun GymTrackerApp() {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        NavHost(navController = nav, startDestination = "login", modifier = Modifier.padding(padding)) {
            composable("login") {
                LoginScreen(
                    onLogin = { user, pass ->
                        scope.launch {
                            val account = Repositorio.iniciarSesion(user, pass)
                            if (account == null) snackbar.showSnackbar("Usuario o contraseña incorrectos")
                            else nav.navigate("home/${account.id}") { popUpTo("login") { inclusive = true } }
                        }
                    },
                    onRegister = { nav.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    onBack = { nav.popBackStack() },
                    onRegister = { account ->
                        scope.launch {
                            if (Repositorio.registrar(account)) {
                                snackbar.showSnackbar("Cuenta creada. Inicia sesión")
                                nav.popBackStack()
                            } else snackbar.showSnackbar("Revisa los datos o prueba otro usuario y correo")
                        }
                    }
                )
            }
            composable("home/{userId}", arguments = listOf(navArgument("userId") { type = NavType.IntType })) { entry ->
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                MenuPrincipalScreen(
                    userId = userId,
                    onAdd = { nav.navigate("add/$userId") },
                    onList = { nav.navigate("routines/$userId") },
                    onProfile = { nav.navigate("profile/$userId") },
                    onLogout = { nav.navigate("login") { popUpTo("home/$userId") { inclusive = true } } }
                )
            }
            composable("add/{userId}", arguments = listOf(navArgument("userId") { type = NavType.IntType })) { entry ->
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                RutinaFormScreen(onBack = { nav.popBackStack() }) { rutina ->
                    scope.launch {
                        if (Repositorio.guardarRutina(rutina.copy(usuarioId = userId))) {
                            snackbar.showSnackbar("Rutina guardada")
                            nav.navigate("routines/$userId") { popUpTo("home/$userId") }
                        } else snackbar.showSnackbar("Completa correctamente los datos de la rutina")
                    }
                }
            }
            composable("routines/{userId}", arguments = listOf(navArgument("userId") { type = NavType.IntType })) { entry ->
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                ListaRutinasScreen(userId, onBack = { nav.popBackStack() }, onAdd = { nav.navigate("add/$userId") },
                    onEdit = { id -> nav.navigate("detail/$id/$userId") })
            }
            composable("detail/{routineId}/{userId}", arguments = listOf(
                navArgument("routineId") { type = NavType.IntType }, navArgument("userId") { type = NavType.IntType }
            )) { entry ->
                val routineId = entry.arguments?.getInt("routineId") ?: return@composable
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                DetalleRutinaScreen(routineId, userId, onBack = { nav.popBackStack() }, onMessage = { scope.launch { snackbar.showSnackbar(it) } })
            }
            composable("profile/{userId}", arguments = listOf(navArgument("userId") { type = NavType.IntType })) { entry ->
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                PerfilUsuarioScreen(userId, onBack = { nav.popBackStack() }, onLogout = {
                    nav.navigate("login") { popUpTo("profile/$userId") { inclusive = true } }
                })
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onRegister: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.FitnessCenter, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Text("GymTracker Pro", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Inicia sesión para continuar", modifier = Modifier.padding(bottom = 20.dp))
        OutlinedTextField(usuario, { usuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Button(onClick = { onLogin(usuario, password) }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Iniciar sesión") }
        TextButton(onClick = onRegister) { Text("¿No tienes cuenta? Regístrate") }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, onRegister: (Usuario) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = onBack) { Text("‹ Volver al Login") }
        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(usuario, { usuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(edad, { edad = it.filter(Char::isDigit) }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(password, { password = it }, label = { Text("Contraseña (mínimo 6 caracteres)") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val years = edad.toIntOrNull() ?: 0
            onRegister(Usuario(nombreUsuario = usuario.trim(), password = password, nombreCompleto = nombre.trim(),
                email = email.trim(), edad = years, fechaRegistro = LocalDate.now().toString()))
        }, modifier = Modifier.fillMaxWidth()) { Text("Registrarme") }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MenuPrincipalScreen(userId: Int, onAdd: () -> Unit, onList: () -> Unit, onProfile: () -> Unit, onLogout: () -> Unit) {
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user by Repositorio.usuarioDao.buscarPorId(userId).collectAsState(initial = null)
    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet {
                Text(user?.nombreCompleto ?: "Socio GymTracker", Modifier.padding(20.dp), style = MaterialTheme.typography.titleLarge)
                NavigationDrawerItem(label = { Text("Inicio") }, selected = true, onClick = { scope.launch { drawer.close() } }, icon = { Icon(Icons.Default.Home, null) })
                NavigationDrawerItem(label = { Text("Agregar rutina") }, selected = false, onClick = { scope.launch { drawer.close(); onAdd() } }, icon = { Icon(Icons.Default.Add, null) })
                NavigationDrawerItem(label = { Text("Mis rutinas") }, selected = false, onClick = { scope.launch { drawer.close(); onList() } }, icon = { Icon(Icons.Default.List, null) })
                NavigationDrawerItem(label = { Text("Mi perfil") }, selected = false, onClick = { scope.launch { drawer.close(); onProfile() } }, icon = { Icon(Icons.Default.Person, null) })
                NavigationDrawerItem(label = { Text("Cerrar sesión") }, selected = false, onClick = onLogout)
            }
        }
    ) {
        Scaffold(topBar = { TopAppBar(title = { Text("GymTracker Pro") }, navigationIcon = {
            IconButton(onClick = { scope.launch { drawer.open() } }) { Icon(Icons.Default.Menu, "Menú") }
        }) }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Hola, ${user?.nombreCompleto ?: "socio"}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("¿Qué deseas hacer hoy?")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HomeCard("Agregar rutina", "Registra un ejercicio", Icons.Default.Add, onAdd, Modifier.weight(1f))
                    HomeCard("Mis rutinas", "Consulta y organiza", Icons.Default.List, onList, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HomeCard("Mi perfil", "Tus datos y progreso", Icons.Default.Person, onProfile, Modifier.weight(1f))
                    HomeCard("Cerrar sesión", "Salir de tu cuenta", Icons.Default.Home, onLogout, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HomeCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.height(150.dp), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RutinaFormScreen(onBack: () -> Unit, onSave: (Rutina) -> Unit) {
    var ejercicio by remember { mutableStateOf("") }
    var grupo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("12") }
    var peso by remember { mutableStateOf("0") }
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    Scaffold(topBar = { TopAppBar(title = { Text("Nueva rutina") }, navigationIcon = { TextButton(onClick = onBack) { Text("‹") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(ejercicio, { ejercicio = it }, label = { Text("Ejercicio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(grupo, { grupo = it }, label = { Text("Grupo muscular") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(series, { series = it.filter(Char::isDigit) }, label = { Text("Series") }, modifier = Modifier.weight(1f))
                OutlinedTextField(reps, { reps = it.filter(Char::isDigit) }, label = { Text("Repeticiones") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(peso, { peso = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Peso (kg)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(fecha, { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { onSave(Rutina(usuarioId = 0, ejercicio = ejercicio.trim(), grupoMuscular = grupo.trim(),
                series = series.toIntOrNull() ?: 0, repeticiones = reps.toIntOrNull() ?: 0,
                pesoKg = peso.toDoubleOrNull() ?: -1.0, fecha = fecha.trim())) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Guardar rutina")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ListaRutinasScreen(userId: Int, onBack: () -> Unit, onAdd: () -> Unit, onEdit: (Int) -> Unit) {
    val rutinas by Repositorio.rutinaDao.listarPorUsuario(userId).collectAsState(initial = emptyList())
    var eliminar by remember { mutableStateOf<Rutina?>(null) }
    val scope = rememberCoroutineScope()
    if (eliminar != null) AlertDialog(onDismissRequest = { eliminar = null },
        title = { Text("Eliminar rutina") }, text = { Text("¿Deseas eliminar ${eliminar!!.ejercicio}?") },
        confirmButton = { TextButton(onClick = { val item = eliminar!!; eliminar = null; scope.launch { Repositorio.rutinaDao.eliminar(item) } }) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = { eliminar = null }) { Text("Cancelar") } })
    Scaffold(topBar = { TopAppBar(title = { Text("Mis rutinas") }, navigationIcon = { TextButton(onClick = onBack) { Text("‹") } }) },
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, "Agregar rutina") } }) { padding ->
        if (rutinas.isEmpty()) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Aún no registras rutinas") }
        else LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(rutinas, key = { it.id }) { item ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.ejercicio, fontWeight = FontWeight.Bold)
                            Text(item.grupoMuscular)
                            Text("${item.series} series × ${item.repeticiones} reps · ${item.pesoKg} kg · ${item.fecha}", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onEdit(item.id) }) { Icon(Icons.Default.Edit, "Editar") }
                        IconButton(onClick = { eliminar = item }) { Icon(Icons.Default.Delete, "Eliminar") }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetalleRutinaScreen(routineId: Int, userId: Int, onBack: () -> Unit, onMessage: (String) -> Unit) {
    var rutina by remember { mutableStateOf<Rutina?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(routineId) { rutina = Repositorio.rutinaDao.buscarPorId(routineId)?.takeIf { it.usuarioId == userId } }
    var ejercicio by remember(rutina) { mutableStateOf(rutina?.ejercicio.orEmpty()) }
    var grupo by remember(rutina) { mutableStateOf(rutina?.grupoMuscular.orEmpty()) }
    var series by remember(rutina) { mutableStateOf(rutina?.series?.toString().orEmpty()) }
    var reps by remember(rutina) { mutableStateOf(rutina?.repeticiones?.toString().orEmpty()) }
    var peso by remember(rutina) { mutableStateOf(rutina?.pesoKg?.toString().orEmpty()) }
    var fecha by remember(rutina) { mutableStateOf(rutina?.fecha.orEmpty()) }
    Scaffold(topBar = { TopAppBar(title = { Text("Editar rutina") }, navigationIcon = { TextButton(onClick = onBack) { Text("‹") } }) }) { padding ->
        val current = rutina
        if (current == null) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No se encontró la rutina") }
        else Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            OutlinedTextField(ejercicio, { ejercicio = it }, label = { Text("Ejercicio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(grupo, { grupo = it }, label = { Text("Grupo muscular") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(series, { series = it.filter(Char::isDigit) }, label = { Text("Series") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(reps, { reps = it.filter(Char::isDigit) }, label = { Text("Repeticiones") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(peso, { peso = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Peso (kg)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(fecha, { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                val updated = current.copy(ejercicio = ejercicio.trim(), grupoMuscular = grupo.trim(), series = series.toIntOrNull() ?: 0,
                    repeticiones = reps.toIntOrNull() ?: 0, pesoKg = peso.toDoubleOrNull() ?: -1.0, fecha = fecha.trim())
                scope.launch {
                    if (Repositorio.guardarRutinaValidada(updated)) { onMessage("Cambios guardados"); onBack() }
                    else onMessage("Revisa los datos de la rutina")
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Actualizar cambios") }
            OutlinedButton(onClick = { scope.launch { Repositorio.rutinaDao.eliminar(current); onBack() } }, modifier = Modifier.fillMaxWidth()) { Text("Eliminar rutina") }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PerfilUsuarioScreen(userId: Int, onBack: () -> Unit, onLogout: () -> Unit) {
    val usuario by Repositorio.usuarioDao.buscarPorId(userId).collectAsState(initial = null)
    val cantidad by Repositorio.rutinaDao.contarPorUsuario(userId).collectAsState(initial = 0)
    val volumen by Repositorio.rutinaDao.volumenPorUsuario(userId).collectAsState(initial = 0.0)
    Scaffold(topBar = { TopAppBar(title = { Text("Mi perfil") }, navigationIcon = { TextButton(onClick = onBack) { Text("‹") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(Icons.Default.Person, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
            Text(usuario?.nombreCompleto ?: "Socio", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("@${usuario?.nombreUsuario.orEmpty()}")
            Text("$cantidad rutinas registradas")
            Text("${"%.1f".format(volumen)} kg de volumen acumulado")
            Text("Email: ${usuario?.email.orEmpty()}")
            Text("Edad: ${usuario?.edad ?: 0} años")
            Text("Miembro desde: ${usuario?.fechaRegistro.orEmpty()}")
            OutlinedButton(onClick = onLogout) { Text("Cerrar sesión") }
        }
    }
}
