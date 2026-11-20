package com.tecsup.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.model.ResultadoGuardado
import com.tecsup.centrotareas.data.HistorialRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.system.measureTimeMillis

class TareasViewModel(private val repo: TareasRepository, private val historialRepo: HistorialRepository? = null) : ViewModel() {
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
    private val _progresoPrimos = MutableStateFlow(0f)
    val progresoPrimos = _progresoPrimos.asStateFlow()
    private val _historial = MutableStateFlow<List<ResultadoGuardado>>(emptyList())
    val historial = _historial.asStateFlow()
    private val tiempos = mutableMapOf<String, Long>()
    private val _diferencia = MutableStateFlow<Pair<Long, Long>?>(null)
    val diferencia = _diferencia.asStateFlow()
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga = _carga.asStateFlow()
    private val _simularError = MutableStateFlow(false)
    val simularError = _simularError.asStateFlow()

    init {
        historialRepo?.let { repoHistorial ->
            viewModelScope.launch { repoHistorial.resultados.collect { _historial.value = it } }
        }
    }

    fun tocar() { _toques.value++ }

    fun tareaLarga() {
        if (_corriendo.value) return
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            _mensaje.value = "Trabajando..."
            try {
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                repeat(100) { paso ->
                    withContext(Dispatchers.Default) { delay(40) }
                    _progreso.value = (paso + 1) / 100f
                }
                Log.d("HILO", "Regresa en ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    fun contar(hasta: Int) {
        if (hasta < 2 || _primos.value == PrimosUi.Calculando) return
        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            try {
                _progresoPrimos.value = 0f
                val cantidad = repo.contarPrimos(hasta) { valor -> _progresoPrimos.value = valor }
                val tiempo = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, tiempo)
                guardarResultado("Primos", "$cantidad hasta $hasta", tiempo)
            } catch (cancelacion: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw cancelacion
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    fun cambiarSimularError(valor: Boolean) { _simularError.value = valor }

    private suspend fun guardarResultado(tipo: String, texto: String, tiempo: Long) {
        val resultado = ResultadoGuardado(tipo, texto, tiempo)
        _historial.value = (listOf(resultado) + _historial.value).take(5)
        historialRepo?.guardar(resultado)
    }

    fun cargarUnaPorUna() = cargar(paralelo = false)
    fun cargarALaVez() = cargar(paralelo = true)
    fun reintentar() = cargarALaVez()

    private fun cargar(paralelo: Boolean) {
        if (_carga.value == CargaUi.Cargando) return
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = withTimeout(8_000) {
                    if (paralelo) {
                        coroutineScope {
                            val perfilDeferred = async { reintentarHastaTres { repo.cargarPerfil() } }
                            val pedidosDeferred = async { reintentarHastaTres { repo.cargarPedidos(_simularError.value) } }
                            perfilDeferred.await() to pedidosDeferred.await()
                        }
                    } else {
                        reintentarHastaTres { repo.cargarPerfil() } to reintentarHastaTres { repo.cargarPedidos(_simularError.value) }
                    }
                }
                val tiempo = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito(
                    if (paralelo) "A la vez" else "Una por una",
                    perfil,
                    pedidos.size,
                    tiempo
                )
                tiempos[if (paralelo) "A la vez" else "Una por una"] = tiempo
                _diferencia.value = tiempos["Una por una"]?.let { secuencial -> tiempos["A la vez"]?.let { secuencial to it } }
                guardarResultado("Carga", "${if (paralelo) "Paralela" else "Secuencial"}: ${pedidos.size} pedidos", tiempo)
            } catch (_: TimeoutCancellationException) {
                _carga.value = CargaUi.Error("Tardó demasiado. Intenta de nuevo.")
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${error.message ?: "Error inesperado"}")
            }
        }
    }

    private suspend fun <T> reintentarHastaTres(block: suspend () -> T): T {
        var ultimo: Exception? = null
        repeat(3) { intento ->
            try {
                return block()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                ultimo = error
                if (intento < 2) delay(300L * (intento + 1))
            }
        }
        throw ultimo ?: IllegalStateException("No se pudo completar la carga")
    }
}


