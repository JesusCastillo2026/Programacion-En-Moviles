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
    @Volatile private var registeringUid: String? = null

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
                        if (registeringUid == user.uid) return@addOnSuccessListener
                        val role = when (doc.getString("rol")) {
                            "admin" -> Role.ADMIN
                            "usuario" -> Role.USER
                            else -> null
                        }
                        if (role == null) {
                            if (doc.exists()) {
                                auth.signOut()
                                trySend(SessionState.Error("Tu cuenta no tiene un rol válido."))
                            }
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

    override suspend fun enviarCorreoRestablecimiento(correo: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(correo.trim()).await()
    }

    override suspend fun registrarUsuario(
        nombre: String,
        telefono: String,
        dni: String,
        correo: String,
        clave: String
    ): Result<Unit> = runCatching {
        val user = auth.createUserWithEmailAndPassword(correo.trim(), clave).await().user
            ?: error("No se pudo crear la cuenta")
        registeringUid = user.uid
        try {
            firestore.collection("usuarios").document(user.uid).set(
                mapOf(
                    "nombre" to nombre.trim(),
                    "telefono" to telefono.trim(),
                    "dni" to dni.trim(),
                    "email" to correo.trim(),
                    "rol" to "usuario",
                    "socio" to false
                )
            ).await()
        } catch (error: Exception) {
            runCatching { user.delete().await() }
            throw error
        } finally {
            auth.signOut()
            registeringUid = null
        }
    }

    override suspend fun perfilActual(): Result<UserProfile> = runCatching {
        val user = auth.currentUser ?: error("Inicia sesión para ver tu perfil")
        val doc = firestore.collection("usuarios").document(user.uid).get().await()
        UserProfile(
            name = doc.getString("nombre").orEmpty(),
            email = user.email.orEmpty(),
            phone = doc.getString("telefono").orEmpty(),
            dni = doc.getString("dni").orEmpty(),
            isMember = doc.getBoolean("socio") ?: false
        )
    }

    override suspend fun actualizarContacto(telefono: String, dni: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Inicia sesión para actualizar tu perfil")
        require(telefono.matches(Regex("^9[0-9]{8}$"))) { "El teléfono debe tener 9 dígitos y comenzar con 9." }
        require(dni.matches(Regex("^[0-9]{8}$"))) { "El DNI debe tener 8 dígitos." }
        firestore.collection("usuarios").document(user.uid)
            .update(mapOf("telefono" to telefono, "dni" to dni)).await()
    }

    override suspend fun guardarReserva(reserva: CinemaReservation): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Inicia sesión para guardar una reserva")
        require(reserva.id.isNotBlank() && reserva.seats.isNotEmpty()) { "La reserva no tiene datos completos." }
        firestore.collection("usuarios").document(user.uid)
            .collection("reservas").document(reserva.id)
            .set(reserva.toFirestoreMap()).await()
    }

    override suspend fun obtenerReservas(): Result<List<CinemaReservation>> = runCatching {
        val user = auth.currentUser ?: error("Inicia sesión para ver tus entradas")
        firestore.collection("usuarios").document(user.uid).collection("reservas")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await().documents.mapNotNull { doc ->
                CinemaReservation(
                    id = doc.id,
                    movieTitle = doc.getString("movieTitle").orEmpty(),
                    venue = doc.getString("venue").orEmpty(),
                    format = doc.getString("format").orEmpty(),
                    date = doc.getString("date").orEmpty(),
                    time = doc.getString("time").orEmpty(),
                    seats = (doc.get("seats") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    total = doc.getDouble("total") ?: 0.0,
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            }
    }

    private fun CinemaReservation.toFirestoreMap(): Map<String, Any> = mapOf(
        "movieTitle" to movieTitle,
        "venue" to venue,
        "format" to format,
        "date" to date,
        "time" to time,
        "seats" to seats,
        "total" to total,
        "createdAt" to createdAt
    )

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

