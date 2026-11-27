package com.tecsup.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.avisoscampus.data.AvisosRepository
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.model.PermisoUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisosViewModel(private val repo: AvisosRepository) : ViewModel() {
    private val _permiso = MutableStateFlow<PermisoUi>(PermisoUi.SinPedir)
    val permiso = _permiso.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private val _eventos = MutableSharedFlow<Aviso>(extraBufferCapacity = 1)
    val eventos = _eventos.asSharedFlow()

    val avisos = repo.avisos
    private val ids = AtomicInteger(1)

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) PermisoUi.Concedido else PermisoUi.Denegado
        if (concedido) _mensaje.value = null
    }

    fun noSePudoMostrar() {
        _mensaje.value = "Sin permiso: el aviso se guardó en el historial, pero no se mostró"
    }

    fun avisarAhora() {
        val aviso = Aviso(
            id = ids.getAndIncrement(),
            titulo = "Tarea lista",
            texto = "Tu entrega fue registrada",
            origen = Origen.BOTON,
            hora = horaActual()
        )
        viewModelScope.launch {
            repo.registrar(aviso)
            _eventos.emit(aviso)
        }
    }

    private fun horaActual(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
}
