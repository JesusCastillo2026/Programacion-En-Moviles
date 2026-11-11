package com.tecsup.cinecastillo.data.auth
import com.tecsup.cinecastillo.domain.model.Session
import com.tecsup.cinecastillo.domain.model.SessionState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<SessionState>
    suspend fun iniciarSesion(correo: String, clave: String): Result<Session>
    suspend fun sesionActual(): Session?
    fun cerrarSesion()
}
