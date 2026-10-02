package com.castillo.navlab.base.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.castillo.navlab.base.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Pantalla Tecsup", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(32.dp))
            Button(onClick = { navController.navigate(Screen.List.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver lista de elementos")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { navController.navigate(Screen.Profile.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Mi perfil")
            }
        }
    }
}

