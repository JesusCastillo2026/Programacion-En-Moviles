package com.tecsup.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {
    private val _toques = MutableStateFlow(0)
    val toques = _toques.asStateFlow()
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje = _mensaje.asStateFlow()
    private val _progreso = MutableStateFlow(0f)
    val progreso = _progreso.asStateFlow()
    private var trabajo: Job? = null
    private val _corriendo = MutableStateFlow(false)
    val corriendo = _corriendo.asStateFlow()
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos = _primos.asStateFlow()
    private var trabajoPrimos: Job? = null
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga = _carga.asStateFlow()
    private val _simularError = MutableStateFlow(false)
    val simularError = _simularError.asStateFlow()

    fun tocar() { _toques.value++ }

    fun tareaLarga() {
        if (_corriendo.value) return
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."
            try {
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                repeat(100) { paso ->
                    withContext(Dispatchers.Default) {
                        if (paso == 0) Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                        delay(40)
                    }
                    _progreso.value = (paso + 1) / 100f
                }
                Log.d("HILO", "Después: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } catch (cancelacion: CancellationException) {
                _mensaje.value = "Cancelado"
                throw cancelacion
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() { trabajo?.cancel() }

    fun contar(hasta: Int) {
        if (hasta < 2 || _primos.value == PrimosUi.Calculando) return
        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta)
                _primos.value = PrimosUi.Listo(cantidad, System.currentTimeMillis() - inicio)
            } catch (cancelacion: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw cancelacion
            }
        }
    }

    fun cancelarPrimos() { trabajoPrimos?.cancel() }
    fun cambiarSimularError(valor: Boolean) { _simularError.value = valor }
    fun cargarUnaPorUna() = cargar(paralelo = false)
    fun cargarALaVez() = cargar(paralelo = true)
    fun reintentar() = cargarALaVez()

    private fun cargar(paralelo: Boolean) {
        if (_carga.value == CargaUi.Cargando) return
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = if (paralelo) {
                    coroutineScope {
                        val perfilTask = async { repo.cargarPerfil() }
                        val pedidosTask = async { repo.cargarPedidos(_simularError.value) }
                        perfilTask.await() to pedidosTask.await()
                    }
                } else {
                    repo.cargarPerfil() to repo.cargarPedidos(_simularError.value)
                }
                _carga.value = CargaUi.Exito(
                    if (paralelo) "A la vez" else "Una por una", perfil, pedidos.size,
                    System.currentTimeMillis() - inicio
                )
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${error.message ?: "Error inesperado"}")
            }
        }
    }
}

