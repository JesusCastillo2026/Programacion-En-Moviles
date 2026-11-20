package com.tecsup.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.centrotareas.data.HistorialRepository
import com.tecsup.centrotareas.data.TareasRepositoryLocal
import com.tecsup.centrotareas.ui.PantallaTareas
import com.tecsup.centrotareas.viewmodel.TareasViewModel
import com.tecsup.centrotareas.viewmodel.TareasViewModelFactory

private val FondoNoche = Color(0xFF0B1117)
private val AzulPanel = Color(0xFF18232D)
private val Turquesa = Color(0xFF58D6C3)
private val TipografiaCentro = Typography(
    headlineSmall = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 21.sp, lineHeight = 27.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 22.sp),
    bodySmall = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium)
)
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
            MaterialTheme(colorScheme = EsquemaCine, typography = TipografiaCentro) {
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
