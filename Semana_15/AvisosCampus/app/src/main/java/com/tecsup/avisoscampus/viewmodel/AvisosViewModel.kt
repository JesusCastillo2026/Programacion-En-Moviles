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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan = _faltan.asStateFlow()
    private var trabajo: Job? = null

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
        viewModelScope.launch {
            registrarYEmitir(crearAviso("Tarea lista", "Tu entrega fue registrada", Origen.BOTON))
        }
    }

    fun recordarEn(segundos: Int = 10) {
        if (trabajo?.isActive == true) return
        trabajo = viewModelScope.launch {
            try {
                for (s in segundos downTo 1) {
                    _faltan.value = s
                    delay(1_000L)
                }
                registrarYEmitir(
                    crearAviso("Recordatorio", "Revisa tu laboratorio pendiente", Origen.RECORDATORIO)
                )
            } finally {
                _faltan.value = null
            }
        }
    }

    fun cancelarRecordatorio() {
        trabajo?.cancel()
    }

    private suspend fun registrarYEmitir(aviso: Aviso) {
        repo.registrar(aviso)
        _eventos.emit(aviso)
    }

    private fun crearAviso(titulo: String, texto: String, origen: Origen) = Aviso(
        id = ids.getAndIncrement(),
        titulo = titulo,
        texto = texto,
        origen = origen,
        hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    )
}
