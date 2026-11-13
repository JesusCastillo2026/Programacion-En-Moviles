package com.tecsup.cinecastillo

import com.tecsup.cinecastillo.data.auth.AuthRepository
import com.tecsup.cinecastillo.domain.model.*
import com.tecsup.cinecastillo.ui.UiState
import com.tecsup.cinecastillo.ui.login.AuthViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @Test fun reintentarTrasRecuperarRedRegresaAlLoginSinDuplicarObservadores() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var conectado = false
            var observadores = 0
            val fake = object : AuthRepository {
                override val session = flow<SessionState> {
                    observadores++
                    try {
                        emit(if (conectado) SessionState.SignedOut else SessionState.Error("Sin conexión"))
                        awaitCancellation()
                    } finally { observadores-- }
                }
                override suspend fun iniciarSesion(correo: String, clave: String): Result<Session> = error("No se debe autenticar al reintentar")
                override suspend fun registrarUsuario(nombre: String, telefono: String, dni: String, correo: String, clave: String): Result<Unit> = Result.success(Unit)
                override suspend fun perfilActual(): Result<UserProfile> = Result.success(UserProfile())
                override suspend fun sesionActual(): Session? = null
                override fun cerrarSesion() {}
            }
            val vm = AuthViewModel(fake)
            advanceUntilIdle()
            assertTrue(vm.estado.value is UiState.Error)
            conectado = true
            vm.reintentarSesion()
            advanceUntilIdle()
            assertEquals(SessionState.SignedOut, (vm.estado.value as UiState.Exito).datos)
            assertEquals(1, observadores)
        } finally { Dispatchers.resetMain() }
    }
}
