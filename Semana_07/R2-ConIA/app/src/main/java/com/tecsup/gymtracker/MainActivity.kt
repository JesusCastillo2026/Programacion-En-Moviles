package com.tecsup.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.gymtracker.data.Repositorio
import com.tecsup.gymtracker.data.Rutina
import com.tecsup.gymtracker.data.Usuario
import com.tecsup.gymtracker.ui.GymTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// La Activity prepara la única conexión local que comparten todas las pantallas.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repositorio.inicializar(applicationContext)
        setContent { GymTheme { GymTrackerApp() } }
    }
}

// Este NavHost conserva un flujo simple y entrega el identificador del socio a cada sección.
@Composable
fun GymTrackerApp() {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { outerPadding ->
        NavHost(navController = nav, startDestination = "login", modifier = Modifier.padding(outerPadding)) {
            composable("login") {
                LoginScreen(
                    onLogin = { username, password ->
                        scope.launch {
                            val socio = Repositorio.iniciarSesion(username, password)
                            if (socio == null) snackbar.showSnackbar("Usuario o contraseña incorrectos")
                            else nav.navigate("home/${socio.id}") { popUpTo("login") { inclusive = true } }
                        }
                    },
                    onRegister = { nav.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(onBack = { nav.popBackStack() }) { socio ->
                    scope.launch {
                        if (Repositorio.registrar(socio)) {
                            nav.popBackStack()
                            snackbar.showSnackbar("Cuenta creada exitosamente. Inicia sesión")
                        } else snackbar.showSnackbar("Revisa tus datos o usa otro usuario y correo")
                    }
                }
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
                RutinaFormScreen(title = "Nueva rutina", onBack = { nav.popBackStack() }) { draft ->
                    scope.launch {
                        if (Repositorio.guardarRutina(draft.copy(usuarioId = userId))) {
                            nav.navigate("routines/$userId") { popUpTo("home/$userId") }
                            snackbar.showSnackbar("Rutina guardada en este dispositivo")
                        } else snackbar.showSnackbar("Completa los campos con valores válidos")
                    }
                }
            }
            composable("routines/{userId}", arguments = listOf(navArgument("userId") { type = NavType.IntType })) { entry ->
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                ListaRutinasScreen(
                    userId = userId,
                    onBack = { nav.popBackStack() },
                    onAdd = { nav.navigate("add/$userId") },
                    onEdit = { routineId -> nav.navigate("detail/$routineId/$userId") }
                )
            }
            composable("detail/{routineId}/{userId}", arguments = listOf(
                navArgument("routineId") { type = NavType.IntType }, navArgument("userId") { type = NavType.IntType }
            )) { entry ->
                val routineId = entry.arguments?.getInt("routineId") ?: return@composable
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                DetalleRutinaScreen(
                    routineId, userId, onBack = { nav.popBackStack() },
                    onMessage = { message -> scope.launch { snackbar.showSnackbar(message) } }
                )
            }
            composable("profile/{userId}", arguments = listOf(navArgument("userId") { type = NavType.IntType })) { entry ->
                val userId = entry.arguments?.getInt("userId") ?: return@composable
                PerfilUsuarioScreen(
                    userId, onBack = { nav.popBackStack() },
                    onLogout = { nav.navigate("login") { popUpTo("home/$userId") { inclusive = true } } }
                )
            }
        }
    }
}

// Login conserva los campos localmente durante la pantalla y deja la consulta en el repositorio.
@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onRegister: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize().background(Color(0xFFF3F6FC)), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp).clip(RoundedCornerShape(28.dp)).background(Color.White).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.FitnessCenter, null, Modifier.size(50.dp), tint = GymBlue)
            Text("GymTracker Pro", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = GymNavy)
            Text("Entrena con intención. Registra cada avance.", color = GymMuted)
            OutlinedTextField(usuario, { usuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
            OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
            Button(onClick = { onLogin(usuario, password) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Iniciar sesión", fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onRegister) { Text("¿Primera vez? Crea tu cuenta") }
        }
    }
}

// El registro crea el socio en SQLite, pero el inicio de sesión queda como un paso explícito.
@Composable
fun RegisterScreen(onBack: () -> Unit, onRegister: (Usuario) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TextButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null); Text(" Volver") }
        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall, color = GymNavy, fontWeight = FontWeight.Bold)
        Text("Tus entrenamientos quedarán asociados a tu perfil.", color = GymMuted)
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
        OutlinedTextField(usuario, { usuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
        OutlinedTextField(edad, { edad = it.filter(Char::isDigit) }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
        OutlinedTextField(password, { password = it }, label = { Text("Contraseña (mínimo 6 caracteres)") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
        Button(onClick = {
            onRegister(Usuario(nombreUsuario = usuario.trim(), password = password, nombreCompleto = nombre.trim(),
                email = email.trim(), edad = edad.toIntOrNull() ?: 0, fechaRegistro = LocalDate.now().toString()))
        }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) { Text("Crear mi cuenta") }
    }
}

// Inicio combina resumen SQL reactivo, acceso rápido y el visual original de esta versión.
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MenuPrincipalScreen(userId: Int, onAdd: () -> Unit, onList: () -> Unit, onProfile: () -> Unit, onLogout: () -> Unit) {
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user by Repositorio.usuarioDao.buscarPorId(userId).collectAsState(initial = null)
    val count by Repositorio.rutinaDao.contarPorUsuario(userId).collectAsState(initial = 0)
    val volume by Repositorio.rutinaDao.volumenPorUsuario(userId).collectAsState(initial = 0.0)
    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.fillMaxWidth().background(GymNavy).padding(22.dp)) {
                    Icon(Icons.Default.FitnessCenter, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(user?.nombreCompleto ?: "Socio GymTracker", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(user?.email.orEmpty(), color = Color.White.copy(alpha = .78f), style = MaterialTheme.typography.bodySmall)
                }
                DrawerAction("Inicio", Icons.Default.Home) { scope.launch { drawer.close() } }
                DrawerAction("Agregar rutina", Icons.Default.Add) { scope.launch { drawer.close(); onAdd() } }
                DrawerAction("Mis rutinas", Icons.Default.List) { scope.launch { drawer.close(); onList() } }
                DrawerAction("Mi perfil", Icons.Default.Person) { scope.launch { drawer.close(); onProfile() } }
                DrawerAction("Cerrar sesión", Icons.Default.Home, onLogout)
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF3F6FC),
            topBar = {
                TopAppBar(
                    title = { Text("GymTracker Pro", color = GymNavy, fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawer.open() } }) { Icon(Icons.Default.Menu, "Abrir menú", tint = GymNavy) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3F6FC))
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Hola, ${user?.nombreCompleto?.substringBefore(' ') ?: "atleta"} 👋", style = MaterialTheme.typography.headlineSmall, color = GymNavy, fontWeight = FontWeight.Bold)
                GymHeroCard(onAdd)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Rutinas", count.toString(), Icons.Default.FitnessCenter, Modifier.weight(1f))
                    MetricCard("Volumen total", "${String.format(Locale.getDefault(), "%.0f", volume)} kg", Icons.Default.BarChart, Modifier.weight(1f))
                }
                Text("Tu espacio", style = MaterialTheme.typography.titleLarge, color = GymNavy, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard("Agregar rutina", "Registra un ejercicio", Icons.Default.Add, onAdd, Modifier.weight(1f))
                    ActionCard("Mis rutinas", "Revisa tu historial", Icons.Default.List, onList, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard("Mi perfil", "Consulta tu progreso", Icons.Default.Person, onProfile, Modifier.weight(1f))
                    ActionCard("Cerrar sesión", "Salir de tu cuenta", Icons.Default.Home, onLogout, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun GymHeroCard(onAdd: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(24.dp)).clickable(onClick = onAdd)) {
        Image(painterResource(R.drawable.gym_hero), contentDescription = "Atleta entrenando en el gimnasio", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(GymNavy.copy(alpha = .96f), GymNavy.copy(alpha = .72f), Color.Transparent))))
        Column(Modifier.align(Alignment.CenterStart).padding(20.dp).widthIn(max = 245.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("ENTRENA A TU RITMO", style = MaterialTheme.typography.labelMedium, color = Color(0xFFB9D4FF), fontWeight = FontWeight.Bold)
            Text("Un registro hoy.\nUn avance mañana.", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.ExtraBold)
            Text("Toca para guardar tu entrenamiento", color = Color.White.copy(alpha = .9f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DrawerAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(label = { Text(label) }, selected = false, onClick = onClick, icon = { Icon(icon, null, tint = GymBlue) }, modifier = Modifier.padding(horizontal = 10.dp))
}

@Composable
private fun MetricCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint = GymBlue, modifier = Modifier.size(25.dp))
            Column {
                Text(value, color = GymNavy, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                Text(label, color = GymMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ActionCard(title: String, detail: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.heightIn(min = 128.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(Color(0xFFEAF1FF)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = GymBlue) }
            Text(title, color = GymNavy, fontWeight = FontWeight.Bold)
            Text(detail, color = GymMuted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Formulario común para altas y ediciones; cada entrada se valida antes de persistir.
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RutinaFormScreen(title: String, onBack: () -> Unit, onSave: (Rutina) -> Unit) {
    var ejercicio by remember { mutableStateOf("") }
    var grupo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("12") }
    var peso by remember { mutableStateOf("0") }
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    val grupos = listOf("Pecho", "Espalda", "Pierna", "Hombro", "Brazos", "Abdomen", "Cardio")
    var menuAbierto by remember { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text(title, color = GymNavy, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Detalles del entrenamiento", style = MaterialTheme.typography.titleLarge, color = GymNavy, fontWeight = FontWeight.Bold)
            OutlinedTextField(ejercicio, { ejercicio = it }, label = { Text("Ejercicio") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
            ExposedDropdownMenuBox(expanded = menuAbierto, onExpandedChange = { menuAbierto = it }) {
                OutlinedTextField(value = grupo, onValueChange = { grupo = it }, readOnly = true,
                    label = { Text("Grupo muscular") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                ExposedDropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    grupos.forEach { opcion -> DropdownMenuItem(text = { Text(opcion) }, onClick = { grupo = opcion; menuAbierto = false }) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(series, { series = it.filter(Char::isDigit) }, label = { Text("Series") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(reps, { reps = it.filter(Char::isDigit) }, label = { Text("Repeticiones") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
            OutlinedTextField(peso, { peso = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Peso (kg)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            OutlinedTextField(fecha, { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") }, leadingIcon = { Icon(Icons.Default.CalendarMonth, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
            Button(onClick = {
                onSave(Rutina(usuarioId = 0, ejercicio = ejercicio.trim(), grupoMuscular = grupo,
                    series = series.toIntOrNull() ?: 0, repeticiones = reps.toIntOrNull() ?: 0,
                    pesoKg = peso.toDoubleOrNull() ?: -1.0, fecha = fecha.trim()))
            }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Guardar rutina", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// El historial combina búsqueda, filtros por grupo muscular, edición y borrado confirmado.
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ListaRutinasScreen(userId: Int, onBack: () -> Unit, onAdd: () -> Unit, onEdit: (Int) -> Unit) {
    val routines by Repositorio.rutinaDao.listarPorUsuario(userId).collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("Todos") }
    var pendingDelete by remember { mutableStateOf<Rutina?>(null) }
    val scope = rememberCoroutineScope()
    val groups = listOf("Todos") + routines.map { it.grupoMuscular }.distinct().sorted()
    val filtered = routines.filter { routine ->
        (group == "Todos" || routine.grupoMuscular == group) &&
            (routine.ejercicio.contains(query, true) || routine.grupoMuscular.contains(query, true))
    }
    pendingDelete?.let { routine ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Eliminar rutina") },
            text = { Text("Se quitará ${routine.ejercicio} de tu historial.") },
            confirmButton = { TextButton(onClick = { pendingDelete = null; scope.launch { Repositorio.rutinaDao.eliminar(routine) } }) { Text("Eliminar", color = Color(0xFFB42318)) } },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") } }
        )
    }
    Scaffold(
        containerColor = Color(0xFFF3F6FC),
        topBar = { TopAppBar(title = { Text("Mis rutinas", color = GymNavy, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }) },
        floatingActionButton = { FloatingActionButton(onClick = onAdd, containerColor = GymBlue, contentColor = Color.White) { Icon(Icons.Default.Add, "Agregar rutina") } }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true,
                    placeholder = { Text("Buscar ejercicio o grupo") }, leadingIcon = { Icon(Icons.Default.Search, null) })
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groups) { item -> FilterChip(selected = group == item, onClick = { group = item }, label = { Text(item) }) }
                }
            }
            item { Text("${filtered.size} registros", color = GymMuted, style = MaterialTheme.typography.labelLarge) }
            if (filtered.isEmpty()) item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.FitnessCenter, null, tint = GymBlue, modifier = Modifier.size(36.dp))
                        Text(if (routines.isEmpty()) "Tu historial empieza aquí" else "No encontramos coincidencias", color = GymNavy, fontWeight = FontWeight.Bold)
                        Text("Agrega una rutina o cambia los filtros.", color = GymMuted)
                    }
                }
            }
            items(filtered, key = { it.id }) { routine ->
                RoutineCard(routine, onEdit = { onEdit(routine.id) }, onDelete = { pendingDelete = routine })
            }
        }
    }
}

@Composable
private fun RoutineCard(routine: Rutina, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFEAF1FF)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.FitnessCenter, null, tint = GymBlue)
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(routine.ejercicio, color = GymNavy, fontWeight = FontWeight.Bold)
                Text(routine.grupoMuscular, color = GymBlue, style = MaterialTheme.typography.labelMedium)
                Text("${routine.series} series × ${routine.repeticiones} reps · ${routine.pesoKg} kg", color = GymMuted, style = MaterialTheme.typography.bodySmall)
                Text(routine.fecha, color = GymMuted, style = MaterialTheme.typography.labelSmall)
            }
            Column {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar rutina", tint = GymBlue) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Eliminar rutina", tint = Color(0xFFB42318)) }
            }
        }
    }
}

// El detalle carga por ID y solo modifica o elimina el registro seleccionado.
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetalleRutinaScreen(routineId: Int, userId: Int, onBack: () -> Unit, onMessage: (String) -> Unit) {
    var routine by remember { mutableStateOf<Rutina?>(null) }
    var askDelete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(routineId) { routine = Repositorio.rutinaDao.buscarPorId(routineId)?.takeIf { it.usuarioId == userId } }
    routine?.let { current ->
        if (askDelete) AlertDialog(
            onDismissRequest = { askDelete = false },
            title = { Text("Eliminar rutina") },
            text = { Text("¿Confirmas que deseas eliminar ${current.ejercicio}?") },
            confirmButton = { TextButton(onClick = { askDelete = false; scope.launch { Repositorio.rutinaDao.eliminar(current); onBack() } }) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { askDelete = false }) { Text("Cancelar") } }
        )
        EditRoutineForm(current, onBack, onDelete = { askDelete = true }, onSave = { updated ->
            scope.launch {
                if (Repositorio.guardarRutinaValidada(updated)) { onMessage("Cambios guardados"); onBack() }
                else onMessage("Revisa los datos del entrenamiento")
            }
        })
    } ?: Scaffold(topBar = { TopAppBar(title = { Text("Detalle de rutina") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No se encontró la rutina") }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditRoutineForm(routine: Rutina, onBack: () -> Unit, onDelete: () -> Unit, onSave: (Rutina) -> Unit) {
    var exercise by remember(routine.id) { mutableStateOf(routine.ejercicio) }
    var group by remember(routine.id) { mutableStateOf(routine.grupoMuscular) }
    var sets by remember(routine.id) { mutableStateOf(routine.series.toString()) }
    var reps by remember(routine.id) { mutableStateOf(routine.repeticiones.toString()) }
    var weight by remember(routine.id) { mutableStateOf(routine.pesoKg.toString()) }
    var date by remember(routine.id) { mutableStateOf(routine.fecha) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Editar rutina", color = GymNavy, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
            actions = { IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFB42318)) } })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF)), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = GymBlue); Text(" Modificando registro existente", color = GymNavy, fontWeight = FontWeight.Medium)
                }
            }
            OutlinedTextField(exercise, { exercise = it }, label = { Text("Ejercicio") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
            OutlinedTextField(group, { group = it }, label = { Text("Grupo muscular") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
            OutlinedTextField(sets, { sets = it.filter(Char::isDigit) }, label = { Text("Series") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(reps, { reps = it.filter(Char::isDigit) }, label = { Text("Repeticiones") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(weight, { weight = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Peso (kg)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(date, { date = it }, label = { Text("Fecha (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
            Button(onClick = { onSave(routine.copy(ejercicio = exercise.trim(), grupoMuscular = group.trim(),
                series = sets.toIntOrNull() ?: 0, repeticiones = reps.toIntOrNull() ?: 0,
                pesoKg = weight.toDoubleOrNull() ?: -1.0, fecha = date.trim())) }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Actualizar cambios", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Perfil recoge estadísticas agregadas directamente desde consultas SQL reactivas.
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PerfilUsuarioScreen(userId: Int, onBack: () -> Unit, onLogout: () -> Unit) {
    val user by Repositorio.usuarioDao.buscarPorId(userId).collectAsState(initial = null)
    val count by Repositorio.rutinaDao.contarPorUsuario(userId).collectAsState(initial = 0)
    val volume by Repositorio.rutinaDao.volumenPorUsuario(userId).collectAsState(initial = 0.0)
    Scaffold(containerColor = Color(0xFFF3F6FC), topBar = {
        TopAppBar(title = { Text("Mi perfil", color = GymNavy, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(88.dp).clip(CircleShape).background(Color(0xFFEAF1FF)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = GymBlue, modifier = Modifier.size(50.dp))
            }
            Text(user?.nombreCompleto ?: "Socio", style = MaterialTheme.typography.headlineSmall, color = GymNavy, fontWeight = FontWeight.Bold)
            Text("@${user?.nombreUsuario.orEmpty()}", color = GymMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Rutinas", count.toString(), Icons.Default.FitnessCenter, Modifier.weight(1f))
                MetricCard("Volumen", "${String.format(Locale.getDefault(), "%.0f", volume)} kg", Icons.Default.BarChart, Modifier.weight(1f))
            }
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileLine("Email", user?.email.orEmpty())
                    ProfileLine("Edad", "${user?.edad ?: 0} años")
                    ProfileLine("Miembro desde", user?.fechaRegistro?.let(::formatDate).orEmpty())
                }
            }
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) { Text("Cerrar sesión") }
        }
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = GymMuted)
        Text(value, color = GymNavy, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatDate(value: String): String = runCatching {
    LocalDate.parse(value).format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "PE")))
}.getOrDefault(value)

private val GymBlue = Color(0xFF2F6BEA)
private val GymNavy = Color(0xFF1B3C8C)
private val GymMuted = Color(0xFF6B7892)
