package com.castillo.campusnav.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.castillo.campusnav.data.CampusStore
import com.castillo.campusnav.screens.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Directory : Screen("directory")
    object Profile : Screen("profile")
    object Detail : Screen("detail/{itemId}") {
        fun createRoute(id: Int) = "detail/$id"
    }
}

/** Mantiene el ID como Int y elimina el acceso anterior al entrar o salir. */
@Composable
fun AppNavigation(store: CampusStore) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen {
                nav.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable(Screen.Home.route) {
            HomeScreen(store,
                onDirectory = { nav.navigate(Screen.Directory.route) },
                onProfile = { nav.navigate(Screen.Profile.route) },
                onLogout = {
                    nav.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Directory.route) {
            DirectoryScreen(store, onBack = { nav.popBackStack() },
                onDetail = { nav.navigate(Screen.Detail.createRoute(it)) })
        }
        composable(Screen.Detail.route, arguments = listOf(navArgument("itemId") { type = NavType.IntType })) { entry ->
            DetailScreen(store, entry.arguments?.getInt("itemId") ?: -1, onBack = { nav.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(store, onBack = { nav.popBackStack() }, onLogout = {
                nav.navigate(Screen.Login.route) { popUpTo(Screen.Home.route) { inclusive = true }; launchSingleTop = true }
            })
        }
    }
}

