package com.tecsup.lab09.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lab09.ui.screens.FavoritesScreen
import com.tecsup.lab09.ui.screens.HomeScreen
import com.tecsup.lab09.ui.screens.PostDetailScreen
import com.tecsup.lab09.ui.screens.TodosScreen
import com.tecsup.lab09.ui.screens.AmphibiansScreen
import com.tecsup.lab09.viewmodel.PostViewModel

private const val HOME = "home"
private const val TODOS = "todos"
private const val FAVORITES = "favorites"
private const val AMPHIBIANS = "amphibians"
private const val DETAIL = "detail/{id}"

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: PostViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: HOME
    val isRoot = route in setOf(HOME, TODOS, FAVORITES, AMPHIBIANS)
    val title = when {
        route.startsWith("detail/") -> "Detalle"
        route == TODOS -> "Tareas"
        route == FAVORITES -> "Favoritos"
        route == AMPHIBIANS -> "Explorar especies"
        else -> "JSONPlaceholder Access"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (!isRoot) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (isRoot) {
                NavigationBar {
                    NavigationBarItem(
                        selected = route == HOME,
                        onClick = { navController.navigate(HOME) { launchSingleTop = true; popUpTo(HOME) { saveState = true }; restoreState = true } },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text("Posts") }
                    )
                    NavigationBarItem(
                        selected = route == TODOS,
                        onClick = { navController.navigate(TODOS) { launchSingleTop = true; popUpTo(HOME) { saveState = true }; restoreState = true } },
                        icon = { Icon(Icons.Filled.Checklist, contentDescription = null) },
                        label = { Text("Tareas") }
                    )
                    NavigationBarItem(
                        selected = route == FAVORITES,
                        onClick = { navController.navigate(FAVORITES) { launchSingleTop = true; popUpTo(HOME) { saveState = true }; restoreState = true } },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text("Favoritos") }
                    )
                    NavigationBarItem(
                        selected = route == AMPHIBIANS,
                        onClick = { navController.navigate(AMPHIBIANS) { launchSingleTop = true; popUpTo(HOME) { saveState = true }; restoreState = true } },
                        icon = { Icon(Icons.Filled.Pets, contentDescription = null) },
                        label = { Text("Especies") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = HOME, modifier = Modifier.padding(padding)) {
            composable(HOME) {
                HomeScreen(viewModel) { id -> navController.navigate("detail/$id") }
            }
            composable(TODOS) { TodosScreen(viewModel) }
            composable(FAVORITES) {
                FavoritesScreen(viewModel) { id -> navController.navigate("detail/$id") }
            }
            composable(AMPHIBIANS) { AmphibiansScreen(viewModel) }
            composable(
                route = DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                PostDetailScreen(entry.arguments?.getInt("id") ?: 0, viewModel)
            }
        }
    }
}
