package com.tecsup.cinecastillo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cinecastillo.data.AuthRepository
import com.tecsup.cinecastillo.data.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val mutableSession = MutableStateFlow<SessionState>(SessionState.Loading)
    val session: StateFlow<SessionState> = mutableSession.asStateFlow()
    private val mutableBusy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = mutableBusy.asStateFlow()
    private val mutableError = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = mutableError.asStateFlow()

    init {
        viewModelScope.launch { repository.session.collect { mutableSession.value = it } }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            mutableError.value = "Escribe tu correo y contraseña."
            return
        }
        viewModelScope.launch {
            mutableBusy.value = true
            mutableError.value = null
            val result = repository.login(email, password)
            mutableBusy.value = false
            if (result.isFailure) mutableError.value = "No se pudo iniciar sesión. Comprueba tus credenciales."
        }
    }

    fun signOut() {
        mutableError.value = null
        repository.signOut()
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(repository) as T
    }
}
