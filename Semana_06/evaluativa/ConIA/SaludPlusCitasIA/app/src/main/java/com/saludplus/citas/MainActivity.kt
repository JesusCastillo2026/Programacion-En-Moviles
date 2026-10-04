package com.saludplus.citas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.saludplus.citas.navigation.AppNavigation
import com.saludplus.citas.ui.theme.SaludPlusTheme

/** Punto de entrada Android: instala el tema y el flujo Compose de SaludPlus. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SaludPlusTheme { AppNavigation() } }
    }
}
