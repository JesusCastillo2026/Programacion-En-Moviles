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
    private val mutableRegistrationFeedback = MutableStateFlow<String?>(null)
    val registrationFeedback: StateFlow<String?> = mutableRegistrationFeedback.asStateFlow()
    private val mutablePasswordResetBusy = MutableStateFlow(false)
    val passwordResetBusy: StateFlow<Boolean> = mutablePasswordResetBusy.asStateFlow()
    private val mutablePasswordResetFeedback = MutableStateFlow<String?>(null)
    val passwordResetFeedback: StateFlow<String?> = mutablePasswordResetFeedback.asStateFlow()
    private val mutablePasswordResetSuccess = MutableStateFlow<Boolean?>(null)
    val passwordResetSuccess: StateFlow<Boolean?> = mutablePasswordResetSuccess.asStateFlow()

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

    fun enviarRestablecimiento(correo: String) {
        val email = correo.trim()
        if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            mutablePasswordResetSuccess.value = false
            mutablePasswordResetFeedback.value = "Escribe un correo válido para recibir el enlace."
            return
        }
        viewModelScope.launch {
            mutablePasswordResetBusy.value = true
            mutablePasswordResetFeedback.value = null
            mutablePasswordResetSuccess.value = null
            val result = repository.enviarCorreoRestablecimiento(email)
            mutablePasswordResetBusy.value = false
            if (result.isSuccess) {
                mutablePasswordResetSuccess.value = true
                mutablePasswordResetFeedback.value = "Si existe una cuenta con ese correo, Firebase enviará un enlace. Revisa Gmail y la carpeta de spam."
            } else {
                mutablePasswordResetSuccess.value = false
                mutablePasswordResetFeedback.value = "No se pudo enviar el correo. Verifica la dirección e inténtalo otra vez."
            }
        }
    }

    fun limpiarFeedbackRestablecimiento() {
        mutablePasswordResetFeedback.value = null
        mutablePasswordResetSuccess.value = null
    }

    fun register(name: String, phone: String, dni: String, email: String, password: String, confirmation: String) {
        val normalizedEmail = email.trim()
        val passwordProblem = passwordProblem(password)
        when {
            name.isBlank() -> mutableError.value = "Escribe tu nombre."
            !normalizedEmail.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) -> mutableError.value = "Escribe un correo válido."
            !phone.matches(Regex("9[0-9]{8}")) -> mutableError.value = "El teléfono debe tener 9 dígitos y comenzar con 9."
            !dni.matches(Regex("[0-9]{8}")) -> mutableError.value = "El DNI debe tener 8 dígitos."
            passwordProblem != null -> mutableError.value = passwordProblem
            password != confirmation -> mutableError.value = "Las contraseñas no coinciden."
            else -> viewModelScope.launch {
                mutableBusy.value = true
                mutableError.value = null
                mutableRegistrationFeedback.value = null
                val result = repository.registrarUsuario(name, phone, dni, normalizedEmail, password)
                mutableBusy.value = false
                if (result.isSuccess) {
                    mutableRegistrationFeedback.value = "Cuenta creada. Inicia sesión con tu correo y contraseña."
                } else {
                    mutableError.value = result.exceptionOrNull()?.localizedMessage
                        ?.takeIf { it.isNotBlank() } ?: "No se pudo crear la cuenta. Revisa la conexión y vuelve a intentar."
                }
            }
        }
    }

    fun clearRegistrationFeedback() { mutableRegistrationFeedback.value = null }
    fun clearError() { mutableError.value = null }

    private fun passwordProblem(password: String): String? = when {
        password.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
        password.none(Char::isLetter) -> "La contraseña debe incluir una letra."
        password.none(Char::isDigit) -> "La contraseña debe incluir un número."
        password.none { !it.isLetterOrDigit() } -> "La contraseña debe incluir un carácter especial."
        else -> null
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

