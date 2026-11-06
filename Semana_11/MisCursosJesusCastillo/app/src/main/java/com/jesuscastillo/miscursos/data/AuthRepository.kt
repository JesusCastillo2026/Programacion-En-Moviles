package com.jesuscastillo.miscursos.data

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val auth: FirebaseAuth) {
    fun iniciarSesion(email: String, password: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                onResult(if (task.isSuccessful) null else mensajeDeError(task.exception, "No se pudo iniciar sesión."))
            }
    }

    fun registrar(email: String, password: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.signOut()
                    onResult(null)
                } else {
                    onResult(mensajeDeError(task.exception, "No se pudo crear la cuenta."))
                }
            }
    }

    fun enviarRestablecimiento(email: String, onResult: (String?) -> Unit) {
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                onResult(if (task.isSuccessful) null else mensajeDeError(task.exception, "No se pudo enviar el correo."))
            }
    }

    fun cerrarSesion() = auth.signOut()

    private fun mensajeDeError(exception: Exception?, alternativo: String): String {
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Ese correo ya tiene una cuenta."
            "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado corta."
            "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND" ->
                "Correo o contraseña incorrectos."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Revisa tu conexión a Internet e inténtalo otra vez."
            else -> alternativo
        }
    }
}
