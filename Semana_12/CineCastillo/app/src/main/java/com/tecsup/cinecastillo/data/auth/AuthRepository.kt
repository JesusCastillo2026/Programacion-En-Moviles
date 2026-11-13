package com.tecsup.cinecastillo.data.auth
import com.tecsup.cinecastillo.domain.model.Session
import com.tecsup.cinecastillo.domain.model.SessionState
import com.tecsup.cinecastillo.domain.model.UserProfile
import com.tecsup.cinecastillo.domain.model.CinemaReservation
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<SessionState>
    suspend fun iniciarSesion(correo: String, clave: String): Result<Session>
    suspend fun enviarCorreoRestablecimiento(correo: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("La recuperación de contraseña no está implementada."))
    suspend fun registrarUsuario(nombre: String, telefono: String, dni: String, correo: String, clave: String): Result<Unit>
    suspend fun perfilActual(): Result<UserProfile>
    suspend fun actualizarContacto(telefono: String, dni: String): Result<Unit> = Result.failure(UnsupportedOperationException("La edición del perfil no está implementada."))
    suspend fun guardarReserva(reserva: CinemaReservation): Result<Unit> = Result.failure(UnsupportedOperationException("El guardado de reservas no está implementado."))
    suspend fun obtenerReservas(): Result<List<CinemaReservation>> = Result.success(emptyList())
    suspend fun sesionActual(): Session?
    fun cerrarSesion()
}
