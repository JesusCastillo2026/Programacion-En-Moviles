package com.tecsup.huellitasvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.navigation.NavVet
import com.tecsup.huellitasvet.ui.theme.VetTheme
import com.tecsup.huellitasvet.viewmodel.SesionViewModel
import com.tecsup.huellitasvet.viewmodel.VetViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val app = application as VetApp
            val sesion: SesionViewModel = viewModel(factory = VetViewModelFactory(app))
            val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
            val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
            val rolTema = when (usuario?.rol) {
                "ADMIN" -> Rol.ADMIN
                "VETERINARIO" -> Rol.VETERINARIO
                else -> Rol.CLIENTE
            }
            VetTheme(rolTema, oscuro) {
                NavVet(sesion, app)
            }
        }
    }
}
