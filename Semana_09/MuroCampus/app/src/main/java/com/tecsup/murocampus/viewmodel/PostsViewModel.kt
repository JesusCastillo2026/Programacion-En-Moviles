package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsViewModel(private val repo: PostRepository) : ViewModel() {
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista = _lista.asStateFlow()
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro = _usuarioFiltro.asStateFlow()
    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    data class DetalleUi(val post: Post, val comentarios: List<Comentario>)
    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Error("Selecciona una publicación."))
    val detalle = _detalle.asStateFlow()
    private var idDetalle: Int? = null

    private val _resultadoCrear = MutableStateFlow<UiState<Post>?>(null)
    val resultadoCrear = _resultadoCrear.asStateFlow()
    private val _resultadoBorrar = MutableStateFlow<UiState<Unit>?>(null)
    val resultadoBorrar = _resultadoBorrar.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            val userId = _usuarioFiltro.value
            (if (userId == null) repo.listar() else repo.porUsuario(userId))
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun filtrar(userId: Int?) {
        if (_usuarioFiltro.value == userId) return
        _usuarioFiltro.value = userId
        cargar()
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun abrir(id: Int) {
        idDetalle = id
        cargarDetalle()
    }

    fun cargarDetalle() {
        val id = idDetalle ?: return
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            try {
                val datos = coroutineScope {
                    val post = async { repo.detalle(id).getOrThrow() }
                    val comentarios = async { repo.comentarios(id).getOrThrow() }
                    DetalleUi(post.await(), comentarios.await())
                }
                _detalle.value = UiState.Exito(datos)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detalle.value = UiState.Error(mensajeDe(e))
            }
        }
    }

    fun publicar(titulo: String, texto: String) {
        if (titulo.isBlank() || texto.isBlank()) return
        viewModelScope.launch {
            _resultadoCrear.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim()))
                .onSuccess { _resultadoCrear.value = UiState.Exito(it) }
                .onFailure { _resultadoCrear.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun limpiarResultadoCrear() { _resultadoCrear.value = null }

    fun borrar(id: Int) {
        viewModelScope.launch {
            _resultadoBorrar.value = UiState.Cargando
            repo.borrar(id)
                .onSuccess { _resultadoBorrar.value = UiState.Exito(Unit) }
                .onFailure { _resultadoBorrar.value = UiState.Error(mensajeDe(it)) }
        }
    }
}
