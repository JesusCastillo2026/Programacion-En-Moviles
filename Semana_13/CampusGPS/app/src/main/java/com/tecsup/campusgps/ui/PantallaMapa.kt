package com.tecsup.campusgps.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.viewmodel.MapaViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val context = LocalContext.current
    val estado by vm.estado.collectAsStateWithLifecycle()
    val lugares by vm.lugares.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) vm.buscar() else vm.permisoDenegado()
    }

    fun alPulsar() {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            coarse == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) vm.buscar() else launcher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("CampusGPS") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = ::alPulsar, enabled = estado !is UbicacionUi.Buscando) {
                Text("Obtener mi ubicación")
            }
            when (val current = estado) {
                UbicacionUi.Inicial -> Text("Pulsa el botón para encontrar tu ubicación.")
                UbicacionUi.Buscando -> CircularProgressIndicator()
                is UbicacionUi.Lista -> Text(
                    "Latitud: ${current.posicion.latitud}\nLongitud: ${current.posicion.longitud}",
                    style = MaterialTheme.typography.bodyLarge
                )
                is UbicacionUi.Error -> Text(current.mensaje, color = MaterialTheme.colorScheme.error)
            }
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
