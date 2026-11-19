package com.tecsup.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.campusgps.data.UbicacionRepository
import com.tecsup.campusgps.data.LugaresRepository
import com.tecsup.campusgps.data.PreferenciasLugarRepository
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.model.TipoError
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.Lugar
import com.tecsup.campusgps.util.distanciaMetros
import com.tecsup.campusgps.util.filtrarPorCategoria
import com.tecsup.campusgps.util.dentroDelRadio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    lugaresRepository: LugaresRepository,
    private val preferenciasRepository: PreferenciasLugarRepository
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
        filtrarPorCategoria(lista, filtro)
            .map { item -> item.copy(dentroDelRadio = metros?.let { dentroDelRadio(item.metros, it) }) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)
    val masCercano = lugares.map { lista ->
        lista.firstOrNull { it.metros != null }?.lugar
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado = _seleccionado.asStateFlow()
    private val _seguimientoActivo = MutableStateFlow(false)
    val seguimientoActivo = _seguimientoActivo.asStateFlow()
    private var seguimientoJob: Job? = null

    init {
        viewModelScope.launch {
            preferenciasRepository.observarUltimoLugar().collect { id ->
                if (_seleccionado.value == null && _lugares.value.any { it.lugar.id == id }) {
                    _seleccionado.value = id
                }
            }
        }
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
        if (id != null) viewModelScope.launch { preferenciasRepository.guardarUltimoLugar(id) }
    }

    fun seleccionarCategoria(categoria: String?) {
        _categoria.value = categoria
        _seleccionado.value = null
    }

    fun seleccionarRadio(metros: Int?) {
        _radio.value = metros
    }

    fun iniciarSeguimiento() {
        if (seguimientoJob?.isActive == true) return
        seguimientoJob = viewModelScope.launch {
            _seguimientoActivo.value = true
            try {
                ubicacionRepo.seguirUbicacion().collect { posicion ->
                    actualizarDistancias(posicion)
                    _estado.value = UbicacionUi.Lista(posicion)
                }
            } catch (error: SecurityException) {
                _estado.value = UbicacionUi.Error("Falta el permiso de ubicación.", TipoError.SIN_PERMISO)
            } catch (error: Exception) {
                _estado.value = UbicacionUi.Error(
                    error.message ?: "No se pudo continuar el seguimiento.",
                    TipoError.OTRO
                )
            } finally {
                _seguimientoActivo.value = false
            }
        }
    }

    fun detenerSeguimiento() {
        seguimientoJob?.cancel()
        seguimientoJob = null
        _seguimientoActivo.value = false
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
