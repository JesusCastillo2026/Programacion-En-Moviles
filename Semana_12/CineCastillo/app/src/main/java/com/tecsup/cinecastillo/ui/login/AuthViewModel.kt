package com.tecsup.cinecastillo.ui.login

import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.ui.UiState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cinecastillo.data.auth.AuthRepository
import com.tecsup.cinecastillo.domain.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val mutableSession = MutableStateFlow<SessionState>(SessionState.Loading)
    val session: StateFlow<SessionState> = mutableSession.asStateFlow()
    val estado = mutableSession.map { when (it) {
        SessionState.Loading -> UiState.Cargando
        is SessionState.Error -> UiState.Error(it.message)
        else -> UiState.Exito(it)
    } }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    private val mutableBusy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = mutableBusy.asStateFlow()
    private val mutableError = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = mutableError.asStateFlow()

    private var observacion: kotlinx.coroutines.Job? = null

    init { reintentarSesion() }

    /** Vuelve a consultar la sesión y la conexión sin duplicar el listener de Firebase. */
    fun reintentarSesion() {
        observacion?.cancel()
        mutableSession.value = SessionState.Loading
        observacion = viewModelScope.launch {
            repository.session.collect { mutableSession.value = it }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            mutableError.value = "Escribe tu correo y contraseña."
            return
        }
        viewModelScope.launch {
            mutableBusy.value = true
            mutableError.value = null
            val result = repository.iniciarSesion(email, password)
            mutableBusy.value = false
            if (result.isFailure) mutableError.value = "No se pudo iniciar sesión. Comprueba tus credenciales."
        }
    }

    fun signOut() {
        mutableError.value = null
        repository.cerrarSesion()
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(container.auth) as T
    }
}

