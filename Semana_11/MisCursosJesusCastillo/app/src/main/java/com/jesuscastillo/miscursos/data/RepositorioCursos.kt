package com.jesuscastillo.miscursos.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
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

    fun guardarCurso(uid: String, curso: Curso, alCompletar: (Exception?) -> Unit) {
        val datos = hashMapOf<String, Any>(
            "titulo" to curso.titulo.trim(),
            "docente" to curso.docente.trim(),
            "creditos" to curso.creditos,
            "descripcion" to curso.descripcion.trim()
        )

        val operacion = if (curso.id.isBlank()) {
            datos["creadoEn"] = FieldValue.serverTimestamp()
            cursosDe(uid).document().set(datos)
        } else {
            cursosDe(uid).document(curso.id).update(datos)
        }

        operacion.addOnCompleteListener { tarea ->
            alCompletar(if (tarea.isSuccessful) null else tarea.exception ?: Exception("No se pudo guardar el curso."))
        }
    }

    fun eliminarCurso(uid: String, cursoId: String, alCompletar: (Exception?) -> Unit) {
        cursosDe(uid).document(cursoId).delete().addOnCompleteListener { tarea ->
            alCompletar(if (tarea.isSuccessful) null else tarea.exception ?: Exception("No se pudo eliminar el curso."))
        }
    }
}
