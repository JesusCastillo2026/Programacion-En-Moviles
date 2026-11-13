package com.tecsup.cinecastillo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.domain.model.UserProfile
import com.tecsup.cinecastillo.domain.model.CinemaReservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = true,
    val profile: UserProfile? = null,
    val reservations: List<CinemaReservation> = emptyList(),
    val error: String? = null,
    val saving: Boolean = false,
    val message: String? = null
)

class ProfileViewModel(private val container: AppContainer) : ViewModel() {
    private val mutableState = MutableStateFlow(ProfileUiState())
    val state = mutableState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            mutableState.value = ProfileUiState(loading = true)
            val profileResult = container.auth.perfilActual()
            val reservationsResult = container.auth.obtenerReservas()
            mutableState.value = profileResult.fold(
                onSuccess = { profile -> ProfileUiState(loading = false, profile = profile, reservations = reservationsResult.getOrDefault(emptyList()), error = reservationsResult.exceptionOrNull()?.localizedMessage) },
                onFailure = { ProfileUiState(loading = false, error = it.localizedMessage ?: "No se pudo cargar el perfil.") }
            )
        }
    }

    fun updateContact(phone: String, dni: String) {
        val current = mutableState.value
        val profile = current.profile ?: return
        viewModelScope.launch {
            mutableState.value = current.copy(saving = true, message = null, error = null)
            val result = container.auth.actualizarContacto(phone, dni)
            mutableState.value = result.fold(
                onSuccess = { current.copy(saving = false, profile = profile.copy(phone = phone, dni = dni), message = "Tus datos se actualizaron.") },
                onFailure = { current.copy(saving = false, error = it.localizedMessage ?: "No se pudieron guardar tus datos.") }
            )
        }
    }

    fun clearMessage() { mutableState.value = mutableState.value.copy(message = null, error = null) }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(container) as T
    }
}
