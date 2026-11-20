package com.tecsup.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.EventoActividad
import com.tecsup.centrotareas.model.EstadoProceso
import com.tecsup.centrotareas.model.ProcesoConcurrenteUi
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.model.ResultadoGuardado
import com.tecsup.centrotareas.data.HistorialRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.system.measureTimeMillis

class TareasViewModel(private val repo: TareasRepository, private val historialRepo: HistorialRepository? = null) : ViewModel() {
    private val limitePrimosConcurrencia = 2_000_000
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
    private val _actividad = MutableStateFlow<List<EventoActividad>>(emptyList())
    val actividad = _actividad.asStateFlow()
    private val _procesos = MutableStateFlow(
        listOf(
            ProcesoConcurrenteUi("progreso", "Tarea con progreso", "Avance simulado durante unos segundos."),
            ProcesoConcurrenteUi("primos_lote", "Búsqueda de primos", "Cuenta los primos hasta 2,000,000 en segundo plano."),
            ProcesoConcurrenteUi("perfil_lote", "Carga de perfil", "Operación suspendida de demostración."),
            ProcesoConcurrenteUi("pedidos_lote", "Carga de pedidos", "Operación independiente con error recuperable.")
        )
    )
    val procesos = _procesos.asStateFlow()
    private val trabajosParalelos = mutableMapOf<String, Job>()
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

    private fun registrarEvento(titulo: String, detalle: String) {
        _actividad.value = (listOf(EventoActividad(titulo, detalle)) + _actividad.value).take(8)
    }

    fun iniciarLoteParalelo() {
        if (_procesos.value.any { it.estado == EstadoProceso.EN_CURSO }) return
        registrarEvento("Panel paralelo iniciado", "Cuatro trabajos comenzaron de forma independiente.")
        iniciarProceso("progreso") { reportar ->
            repeat(50) { paso ->
                delay(70)
                reportar((paso + 1) / 50f)
            }
            "Tarea terminada sin bloquear la pantalla."
        }
        iniciarProceso("primos_lote") { reportar ->
            val cantidad = repo.contarPrimos(limitePrimosConcurrencia, reportar)
            "Se encontraron $cantidad números primos."
        }
        iniciarProceso("perfil_lote") {
            val perfil = repo.cargarPerfil()
            "Perfil cargado: $perfil."
        }
        iniciarProceso("pedidos_lote") {
            val pedidos = repo.cargarPedidos(_simularError.value)
            "Se cargaron ${pedidos.size} pedidos."
        }
    }

    fun cancelarProceso(id: String) {
        trabajosParalelos[id]?.cancel()
    }

    fun cancelarLoteParalelo() {
        trabajosParalelos.values.filter { it.isActive }.forEach(Job::cancel)
    }

    fun reintentarProceso(id: String) {
        if (_procesos.value.any { it.id == id && it.estado == EstadoProceso.EN_CURSO }) return
        when (id) {
            "progreso" -> iniciarProceso(id) { reportar ->
                repeat(50) { paso ->
                    delay(70)
                    reportar((paso + 1) / 50f)
                }
                "Tarea terminada sin bloquear la pantalla."
            }
            "primos_lote" -> iniciarProceso(id) { reportar ->
                val cantidad = repo.contarPrimos(limitePrimosConcurrencia, reportar)
                "Se encontraron $cantidad números primos."
            }
            "perfil_lote" -> iniciarProceso(id) {
                "Perfil cargado: ${repo.cargarPerfil()}."
            }
            "pedidos_lote" -> iniciarProceso(id) {
                val pedidos = repo.cargarPedidos(_simularError.value)
                "Se cargaron ${pedidos.size} pedidos."
            }
        }
    }

    private fun iniciarProceso(id: String, operacion: suspend ((Float) -> Unit) -> String) {
        val proceso = _procesos.value.firstOrNull { it.id == id } ?: return
        _procesos.update { lista ->
            lista.map {
                if (it.id == id) it.copy(estado = EstadoProceso.EN_CURSO, progreso = if (id == "perfil_lote" || id == "pedidos_lote") null else 0f, tiempoMs = null, resultado = null)
                else it
            }
        }
        registrarEvento("${proceso.titulo} iniciado", "Este trabajo puede detenerse sin cancelar los demás.")
        val inicio = System.currentTimeMillis()
        trabajosParalelos[id] = viewModelScope.launch {
            try {
                val resultado = operacion { valor ->
                    _procesos.update { lista -> lista.map { if (it.id == id) it.copy(progreso = valor) else it } }
                }
                val tiempo = System.currentTimeMillis() - inicio
                _procesos.update { lista ->
                    lista.map { if (it.id == id) it.copy(estado = EstadoProceso.COMPLETADO, progreso = 1f, tiempoMs = tiempo, resultado = resultado) else it }
                }
                registrarEvento("${proceso.titulo} completado", "Terminó en ${tiempo} ms.")
            } catch (cancelacion: CancellationException) {
                _procesos.update { lista ->
                    lista.map { if (it.id == id) it.copy(estado = EstadoProceso.CANCELADO, resultado = "Cancelado; puedes volver a iniciarlo.") else it }
                }
                registrarEvento("${proceso.titulo} cancelado", "Los otros trabajos continúan sin cambios.")
                throw cancelacion
            } catch (error: Exception) {
                _procesos.update { lista ->
                    lista.map { if (it.id == id) it.copy(estado = EstadoProceso.ERROR, resultado = error.message ?: "No se pudo completar.") else it }
                }
                registrarEvento("${proceso.titulo} con error", error.message ?: "Puedes volver a intentarlo.")
            }
        }
    }

    fun tareaLarga() {
        if (_corriendo.value) return
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."
            registrarEvento("Tarea iniciada", "El trabajo se ejecuta fuera del hilo principal.")
            try {
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                repeat(100) { paso ->
                    withContext(Dispatchers.Default) { delay(40) }
                    _progreso.value = (paso + 1) / 100f
                    if ((paso + 1) % 25 == 0) {
                        registrarEvento("Tarea en progreso", "Avance: ${paso + 1}%.")
                    }
                }
                Log.d("HILO", "Regresa en ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
                registrarEvento("Tarea completada", "La interfaz siguió disponible durante la ejecución.")
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        if (_corriendo.value) registrarEvento("Tarea cancelada", "Puedes iniciarla nuevamente cuando quieras.")
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    fun contar(hasta: Int) {
        if (hasta < 2 || _primos.value == PrimosUi.Calculando) return
        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            registrarEvento("Cálculo de primos iniciado", "Buscando números primos hasta $hasta.")
            try {
                _progresoPrimos.value = 0f
                val cantidad = repo.contarPrimos(hasta) { valor -> _progresoPrimos.value = valor }
                val tiempo = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, tiempo)
                guardarResultado("Primos", "$cantidad hasta $hasta", tiempo)
                registrarEvento("Cálculo completado", "$cantidad primos encontrados en ${tiempo} ms.")
            } catch (cancelacion: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                registrarEvento("Cálculo cancelado", "La pantalla permaneció disponible.")
                throw cancelacion
            }
        }
    }

    fun cancelarPrimos() {
        if (_primos.value == PrimosUi.Calculando) {
            registrarEvento("Cancelación solicitada", "Deteniendo el cálculo de números primos.")
        }
        trabajoPrimos?.cancel()
    }

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
        registrarEvento(if (valor) "Error de red activado" else "Error de red desactivado", "Modo de demostración.")
    }

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
            val modo = if (paralelo) "paralela" else "secuencial"
            registrarEvento("Carga $modo iniciada", if (paralelo) "Perfil y pedidos se solicitan al mismo tiempo." else "Perfil y pedidos se solicitan uno después del otro.")
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
                registrarEvento("Carga completada", "Estrategia $modo · ${tiempo} ms.")
            } catch (_: TimeoutCancellationException) {
                _carga.value = CargaUi.Error("Tardó demasiado. Intenta de nuevo.")
                registrarEvento("Tiempo agotado", "La carga superó el límite de espera.")
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${error.message ?: "Error inesperado"}")
                registrarEvento("Carga fallida", error.message ?: "Ocurrió un error inesperado.")
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


