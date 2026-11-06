package com.jesuscastillo.miscursos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.jesuscastillo.miscursos.data.AuthRepository
import com.jesuscastillo.miscursos.ui.AuthScreen
import com.jesuscastillo.miscursos.ui.CursosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MisCursosApp() }
    }
}

@androidx.compose.runtime.Composable
private fun MisCursosApp() {
    val auth = remember { FirebaseAuth.getInstance() }
    val repository = remember { AuthRepository(auth) }
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { currentUser = it.currentUser }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6650A4),
            secondary = Color(0xFF00A9D6),
            background = Color(0xFFFCFCFD),
            surface = Color(0xFFFFFFFF)
        )
    ) {
        Surface {
            val user = currentUser
            if (user == null) {
                AuthScreen(repository)
            } else {
                CursosScreen(user, onCerrarSesion = repository::cerrarSesion)
            }
        }
    }
}
