package com.tecsup.cinecastillo.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

import com.tecsup.cinecastillo.domain.model.*

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val conexionDisponible: () -> Boolean
) : AuthRepository {
    override val session: Flow<SessionState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // Una instalación nueva aún no puede autenticarse ni tener caché sin red.
                trySend(if (conexionDisponible()) SessionState.SignedOut else
                    SessionState.Error("Sin conexión. Conéctate a Internet para iniciar sesión y descargar el catálogo."))
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

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Session> = runCatching {
        val user = auth.signInWithEmailAndPassword(correo.trim(), clave).await().user
            ?: error("No se obtuvo la sesión")
        leerSesion(user.uid, user.email.orEmpty())
    }

    override suspend fun sesionActual(): Session? {
        val user = auth.currentUser ?: return null
        return leerSesion(user.uid, user.email.orEmpty())
    }

    private suspend fun leerSesion(uid: String, email: String): Session {
        val doc = firestore.collection("usuarios").document(uid).get().await()
        val role = when (doc.getString("rol")) {
            "admin" -> Role.ADMIN
            "usuario" -> Role.USER
            else -> error("Rol inválido")
        }
        return Session(uid, email, role)
    }

    override fun cerrarSesion() = auth.signOut()
}

