package com.tecsup.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.campusgps.data.UbicacionRepository
import com.tecsup.campusgps.data.LugaresRepository
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.model.TipoError
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.Lugar
import com.tecsup.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    lugaresRepository: LugaresRepository
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado = _estado.asStateFlow()
    private val _lugares = MutableStateFlow(
        lugaresRepository.obtenerLugares().map { com.tecsup.campusgps.model.LugarConDistancia(it, null) }
    )
    private val _categoria = MutableStateFlow<String?>(null)
    val categoria = _categoria.asStateFlow()
    private val _radio = MutableStateFlow<Int?>(null)
    val radio = _radio.asStateFlow()
    val lugares = combine(_lugares, _categoria, _radio) { lista, filtro, metros ->
        lista.filter { filtro == null || it.lugar.categoria == filtro }
            .map { item -> item.copy(dentroDelRadio = metros?.let { item.metros != null && item.metros <= it }) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)
    val masCercano = lugares.map { lista ->
        lista.firstOrNull { it.metros != null }?.lugar
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado = _seleccionado.asStateFlow()

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    fun seleccionarCategoria(categoria: String?) {
        _categoria.value = categoria
        _seleccionado.value = null
    }

    fun seleccionarRadio(metros: Int?) {
        _radio.value = metros
    }

    fun buscar() {
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services.",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }
        if (!ubicacionRepo.ubicacionActiva()) {
            _estado.value = UbicacionUi.Error(
                "Activa el GPS e intenta de nuevo.",
                TipoError.GPS_APAGADO
            )
            return
        }
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    actualizarDistancias(it)
                    _estado.value = UbicacionUi.Lista(it)
                }
                .onFailure {
                    val tipo = if (it is SecurityException) TipoError.SIN_PERMISO else TipoError.OTRO
                    val mensaje = if (tipo == TipoError.SIN_PERMISO) "Falta el permiso de ubicación."
                    else it.message ?: "No se pudo obtener la ubicación."
                    _estado.value = UbicacionUi.Error(mensaje, tipo)
                }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás.",
            TipoError.SIN_PERMISO
        )
    }

    private fun actualizarDistancias(posicion: com.tecsup.campusgps.model.Posicion) {
        _lugares.value = _lugares.value
            .map { LugarConDistancia(it.lugar, distanciaMetros(posicion, it.lugar)) }
            .sortedBy { it.metros }
    }
}
