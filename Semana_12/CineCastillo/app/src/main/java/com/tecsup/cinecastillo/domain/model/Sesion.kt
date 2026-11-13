package com.tecsup.cinecastillo.domain.model

data class Session(val uid: String, val email: String, val role: Role)
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dni: String = "",
    val isMember: Boolean = false
)

data class CinemaReservation(
    val id: String = "",
    val movieTitle: String = "",
    val venue: String = "",
    val format: String = "",
    val date: String = "",
    val time: String = "",
    val seats: List<String> = emptyList(),
    val total: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
enum class Role { ADMIN, USER }

sealed interface SessionState {
    data object Loading : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val session: Session) : SessionState
    data class Error(val message: String) : SessionState
}

