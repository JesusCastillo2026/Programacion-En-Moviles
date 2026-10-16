package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: VetRepository,
    private val prefs: PreferenciasStore,
    listo: Flow<Boolean>
) : ViewModel() {
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando
    private val _modoOscuro = MutableStateFlow(false)
    val modoOscuro: StateFlow<Boolean> = _modoOscuro

    init {
        viewModelScope.launch { prefs.modoOscuro.collect { _modoOscuro.value = it } }
        viewModelScope.launch {
            combine(prefs.usuarioId, prefs.perfilActivo, listo) { id, rol, listoYa -> Triple(id, rol, listoYa) }
                .collectLatest { (id, rol, listoYa) ->
                    if (listoYa) {
                        val usuario = if (id > 0) repo.obtenerUsuario(id) else null
                        _usuarioActual.value = usuario?.takeIf { it.rol == rol }
                        _cargando.value = false
                    }
                }
        }
    }

    fun login(correo: String, clave: String, rol: Rol) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa correo y contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correo, clave)
            if (usuario == null) _error.value = "Correo o contraseña incorrectos"
            else if (usuario.rol != rol.name) _error.value = "Elige el perfil correcto"
            else {
                _error.value = null
                prefs.guardarSesion(usuario.rol, usuario.id)
            }
        }
    }

    fun registrarCliente(nombre: String, correo: String, clave: String, onExito: () -> Unit) {
        val email = correo.trim()
        if (nombre.isBlank() || email.isBlank() || clave.isBlank()) {
            _error.value = "Completa todos los campos"
            return
        }
        if (!email.matches(Regex("^[A-Za-z0-9+._%\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$"))) {
            _error.value = "Escribe un correo válido"
            return
        }
        if (!clave.matches(Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S{8,}$"))) {
            _error.value = "La contraseña no cumple los requisitos de seguridad"
            return
        }
        viewModelScope.launch {
            runCatching {
                repo.registrarUsuario(Usuario(nombre = nombre.trim(), correo = email, clave = clave, rol = Rol.CLIENTE.name))
            }.onSuccess { registrado ->
                if (registrado) {
                    _error.value = null
                    onExito()
                } else {
                    _error.value = "Ya existe una cuenta con ese correo"
                }
            }.onFailure {
                _error.value = "No se pudo crear la cuenta. Inténtalo nuevamente"
            }
        }
    }

    fun cambiarPerfil() {
        viewModelScope.launch {
            val id = if (_usuarioActual.value?.rol == Rol.ADMIN.name) 1 else 3
            repo.obtenerUsuario(id)?.let { prefs.guardarSesion(it.rol, it.id) }
        }
    }

    fun cerrarSesion() { viewModelScope.launch { prefs.cerrarSesion() } }
    fun guardarModoOscuro(valor: Boolean) { viewModelScope.launch { prefs.guardarModoOscuro(valor) } }
    fun limpiarError() { _error.value = null }
}
