package com.jesuscastillo.miscursos.data

import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val auth: FirebaseAuth) {
    fun iniciarSesion(email: String, password: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                onResult(if (task.isSuccessful) null else task.exception?.message ?: "No se pudo iniciar sesión.")
            }
    }

    fun registrar(email: String, password: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.signOut()
                    onResult(null)
                } else {
                    onResult(task.exception?.message ?: "No se pudo crear la cuenta.")
                }
            }
    }

    fun enviarRestablecimiento(email: String, onResult: (String?) -> Unit) {
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                onResult(if (task.isSuccessful) null else task.exception?.message ?: "No se pudo enviar el correo.")
            }
    }

    fun cerrarSesion() = auth.signOut()
}
