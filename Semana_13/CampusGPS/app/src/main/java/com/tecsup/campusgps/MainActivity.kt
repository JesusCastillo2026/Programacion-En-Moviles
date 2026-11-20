package com.tecsup.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.maplibre.android.MapLibre
import com.tecsup.campusgps.data.UbicacionRepositoryGoogle
import com.tecsup.campusgps.data.LugaresRepositoryLocal
import com.tecsup.campusgps.data.PreferenciasLugarRepositoryDataStore
import com.tecsup.campusgps.data.ValhallaRutasRepository
import com.tecsup.campusgps.ui.PantallaMapa
import com.tecsup.campusgps.viewmodel.CampusViewModelFactory
import com.tecsup.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(applicationContext)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF55D6C2),
                    onPrimary = Color(0xFF06201D),
                    secondary = Color(0xFFC4E879),
                    secondaryContainer = Color(0xFF254C47),
                    onSecondaryContainer = Color(0xFFD0FFF5),
                    background = Color(0xFF091116),
                    surface = Color(0xFF131E24),
                    surfaceVariant = Color(0xFF202D34),
                    onSurface = Color(0xFFF2F6F7),
                    onSurfaceVariant = Color(0xFFB5C3C8)
                )
            ) {
                Surface {
                    val vm: MapaViewModel = viewModel(
                        factory = CampusViewModelFactory(
                            UbicacionRepositoryGoogle(applicationContext),
                            LugaresRepositoryLocal(),
                            PreferenciasLugarRepositoryDataStore(applicationContext),
                            ValhallaRutasRepository()
                        )
                    )
                    PantallaMapa(vm)
                }
            }
        }
    }
}
