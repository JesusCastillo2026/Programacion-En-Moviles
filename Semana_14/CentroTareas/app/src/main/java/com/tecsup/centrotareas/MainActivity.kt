package com.tecsup.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.centrotareas.data.TareasRepositoryLocal
import com.tecsup.centrotareas.ui.PantallaTareas
import com.tecsup.centrotareas.viewmodel.TareasViewModel
import com.tecsup.centrotareas.viewmodel.TareasViewModelFactory

private val AzulTecsup = Color(0xFF1565A8)
private val EsquemaBase = lightColorScheme(
    primary = AzulTecsup,
    secondary = Color(0xFF315B80),
    tertiary = Color(0xFF3B6F9C)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = EsquemaBase) {
                Surface {
                    val vm: TareasViewModel = viewModel(factory = TareasViewModelFactory(TareasRepositoryLocal()))
                    PantallaTareas(vm)
                }
            }
        }
    }
}
