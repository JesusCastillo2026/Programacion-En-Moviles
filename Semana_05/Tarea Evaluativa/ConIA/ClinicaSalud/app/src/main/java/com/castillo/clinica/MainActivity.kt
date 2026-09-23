package com.castillo.clinica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { ReservationApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationApp() {
    val nav = rememberNavController()
    val bookings = remember { mutableStateListOf<Booking>().apply {addAll(initialBookings())} }
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sections = listOf("home" to "Inicio", "bookings" to if(clinic) "Mis citas" else "Reservas",
        "extra" to if(clinic) "Historial médico" else "Rutinas", "profile" to "Perfil")
    fun goTo(destination: String) {
        nav.navigate(destination) {
            popUpTo("home") {saveState=true}
            launchSingleTop=true
            restoreState=true
        }
    }
    val primary = sections.any {it.first==route}
    val title = when {
        route=="home" -> appTitle
        route=="bookings" -> if(clinic) "Mis citas" else "Mis reservas"
        route=="extra" -> if(clinic) "Historial médico" else "Rutinas"
        route=="profile" -> "Mi perfil"
        route.startsWith("detail") -> if(clinic) "Perfil del médico" else "Detalle de clase"
        route.startsWith("book/") -> if(clinic) "Agendar cita" else "Elegir horario"
        else -> "Confirmación"
    }
    val body: @Composable () -> Unit = {
        Scaffold(
            topBar = { TopAppBar(
                title={Column {Text(title,fontWeight=FontWeight.Bold)
                    if(route=="home") Text("Hola, ${person.substringBefore(" ")}",style=MaterialTheme.typography.labelSmall)}},
                navigationIcon={
                    if(clinic && primary) TextButton(onClick={scope.launch {drawer.open()}}) {
                        Text("☰",color=if(route=="home") Color.White else accent)
                    } else if(!primary) TextButton(onClick={nav.popBackStack()}) {Text("←")}
                },
                colors=TopAppBarDefaults.topAppBarColors(
                    containerColor=if(route=="home") accent else Color.White,
                    titleContentColor=if(route=="home") Color.White else Color(0xFF222222))) },
            bottomBar = {
                if(!clinic && primary) NavigationBar(containerColor=Color.White) {
                    sections.forEach { (destination,label) ->
                        NavigationBarItem(selected=route==destination,onClick={goTo(destination)},
                            icon={Text(if(route==destination) "●" else "○")},
                            label={Text(label)}, colors=NavigationBarItemDefaults.colors(
                                selectedIconColor=accent,selectedTextColor=accent,indicatorColor=tint))
                    }
                }
            }
        ) { padding ->
            NavHost(nav,startDestination="home",modifier=Modifier.fillMaxSize().padding(padding)
                .background(if(enhanced) Color(0xFFF7F8FC) else Color.White)) {
                composable("home") { HomeScreen {nav.navigate("detail/$it")} }
                composable("detail/{id}",arguments=listOf(navArgument("id"){type=NavType.IntType})) { state ->
                    catalog.firstOrNull {it.id==state.arguments?.getInt("id")}?.let { item ->
                        DetailScreen(item) {nav.navigate("book/${item.id}")}
                    } ?: EmptyMessage("Opción no encontrada", "Vuelve al inicio.")
                }
                composable("book/{id}",arguments=listOf(navArgument("id"){type=NavType.IntType})) { state ->
                    catalog.firstOrNull {it.id==state.arguments?.getInt("id")}?.let { item ->
                        BookingScreen(item,bookings) { date,time ->
                            if(!isDuplicate(bookings,item,date,time) && (clinic || availableSeats(bookings,item,date,time)>0)) {
                                val id=(bookings.maxOfOrNull {it.id} ?: 0)+1
                                bookings.add(0,Booking(id,item.id,item.name,date,time))
                                nav.navigate("confirmation/$id") {popUpTo("home")}
                            }
                        }
                    }
                }
                composable("confirmation/{id}",arguments=listOf(navArgument("id"){type=NavType.IntType})) { state ->
                    ConfirmationScreen(bookings.firstOrNull {it.id==state.arguments?.getInt("id")},
                        onBookings={goTo("bookings")},onHome={goTo("home")})
                }
                composable("bookings") {BookingsScreen(bookings) {id ->
                    val index=bookings.indexOfFirst {it.id==id}
                    if(index>=0 && bookings[index].status=="Confirmada")
                        bookings[index]=bookings[index].copy(status="Cancelada")
                }}
                composable("extra") {ExtraScreen(bookings)}
                composable("profile") {ProfileScreen(bookings)}
            }
        }
    }
    if(clinic) ModalNavigationDrawer(drawerState=drawer,drawerContent={
        ModalDrawerSheet {
            Column(Modifier.padding(24.dp)) {Text(person,fontWeight=FontWeight.Bold); Text("Paciente")}
            HorizontalDivider()
            sections.forEach { (destination,label) ->
                NavigationDrawerItem(label={Text(label)},selected=route==destination,
                    icon={Text(if(route==destination) "●" else "○")},
                    onClick={goTo(destination);scope.launch {drawer.close()}},
                    modifier=Modifier.padding(horizontal=12.dp,vertical=4.dp))
            }
        }
    },content=body) else body()
}

