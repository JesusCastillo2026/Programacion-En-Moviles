package com.jesuscastillo.miscursos.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RepositorioCursos(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private fun cursosDe(uid: String) =
        firestore.collection("usuarios").document(uid).collection("cursos")

    fun observarCursos(
        uid: String,
        alCambiar: (List<Curso>) -> Unit,
        alFallar: (Exception) -> Unit
    ): ListenerRegistration {
        return cursosDe(uid)
            .orderBy("titulo")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    alFallar(error)
                } else {
                    alCambiar(
                        snapshot?.documents.orEmpty().map { documento ->
                            Curso(
                                id = documento.id,
                                titulo = documento.getString("titulo").orEmpty(),
                                docente = documento.getString("docente").orEmpty(),
                                creditos = documento.getLong("creditos")?.toInt() ?: 0,
                                descripcion = documento.getString("descripcion").orEmpty()
                            )
                        }
                    )
                }
            }
    }
}
