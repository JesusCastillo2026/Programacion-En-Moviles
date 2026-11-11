package com.tecsup.cinecastillo.domain.model

data class Session(val uid: String, val email: String, val role: Role)
enum class Role { ADMIN, USER }

sealed interface SessionState {
    data object Loading : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val session: Session) : SessionState
    data class Error(val message: String) : SessionState
}

