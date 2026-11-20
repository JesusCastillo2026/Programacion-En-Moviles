package com.tecsup.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.centrotareas.data.HistorialRepository
import com.tecsup.centrotareas.data.TareasRepositoryLocal
import com.tecsup.centrotareas.ui.PantallaTareas
import com.tecsup.centrotareas.viewmodel.TareasViewModel
import com.tecsup.centrotareas.viewmodel.TareasViewModelFactory

private val FondoNoche = Color(0xFF0B1117)
private val AzulPanel = Color(0xFF18232D)
private val Turquesa = Color(0xFF58D6C3)
private val EsquemaCine = darkColorScheme(
    primary = Turquesa,
    secondary = Color(0xFF8BA6FF),
    tertiary = Color(0xFFFFC76A),
    background = FondoNoche,
    surface = AzulPanel,
    surfaceVariant = Color(0xFF202D37)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = EsquemaCine) {
                Surface(color = FondoNoche) {
                    val vm: TareasViewModel = viewModel(
                        factory = TareasViewModelFactory(
                            TareasRepositoryLocal(), HistorialRepository(applicationContext)
                        )
                    )
                    PantallaTareas(vm)
                }
            }
        }
    }
}
