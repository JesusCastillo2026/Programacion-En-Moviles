package com.tecsup.cinecastillo.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.cinecastillo.AppContainer
import com.tecsup.cinecastillo.domain.model.CinemaReservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookingUiState(
    val saving: Boolean = false,
    val savedReservation: CinemaReservation? = null,
    val error: String? = null
)

class BookingViewModel(private val container: AppContainer) : ViewModel() {
    private val mutableState = MutableStateFlow(BookingUiState())
    val state = mutableState.asStateFlow()

    fun confirm(reservation: CinemaReservation) {
        if (mutableState.value.saving) return
        viewModelScope.launch {
            mutableState.value = BookingUiState(saving = true)
            val result = container.auth.guardarReserva(reservation)
            mutableState.value = result.fold(
                onSuccess = { BookingUiState(savedReservation = reservation) },
                onFailure = { BookingUiState(error = it.localizedMessage ?: "No se pudo guardar la reserva.") }
            )
        }
    }

    fun clearResult() { mutableState.value = BookingUiState() }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = BookingViewModel(container) as T
    }
}
