package com.tecsup.cinecastillo.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class Session(val uid: String, val email: String, val role: Role)
enum class Role { ADMIN, USER }

sealed interface SessionState {
    data object Loading : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val session: Session) : SessionState
    data class Error(val message: String) : SessionState
}

interface AuthRepository {
    val session: Flow<SessionState>
    suspend fun login(email: String, password: String): Result<Unit>
    fun signOut()
}

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override val session: Flow<SessionState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                trySend(SessionState.SignedOut)
            } else {
                trySend(SessionState.Loading)
                firestore.collection("usuarios").document(user.uid).get()
                    .addOnSuccessListener { doc ->
                        if (auth.currentUser?.uid != user.uid) return@addOnSuccessListener
                        val role = when (doc.getString("rol")) {
                            "admin" -> Role.ADMIN
                            "usuario" -> Role.USER
                            else -> null
                        }
                        if (role == null) {
                            auth.signOut()
                            trySend(SessionState.Error("Tu cuenta no tiene un rol válido."))
                        } else {
                            trySend(SessionState.SignedIn(Session(user.uid, user.email.orEmpty(), role)))
                        }
                    }
                    .addOnFailureListener {
                        if (auth.currentUser?.uid != user.uid) return@addOnFailureListener
                        auth.signOut()
                        trySend(SessionState.Error("No se pudo verificar tu rol. Revisa la conexión."))
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override fun signOut() = auth.signOut()
}

