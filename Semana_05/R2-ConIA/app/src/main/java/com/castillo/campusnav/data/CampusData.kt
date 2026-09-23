package com.castillo.campusnav.data

import java.text.Normalizer

/** Los registros son ejemplos locales, no expedientes de una institución real. */
data class Student(val id: Int, val name: String, val career: String, val area: String, val bio: String)

val students = listOf(
    Student(1, "Jesus Castillo Sumire", "Programación en Móviles", "Tecnología", "Aprendiendo a crear experiencias Android con Kotlin y Jetpack Compose."),
    Student(2, "María García", "Arquitectura", "Diseño", "Explora espacios sostenibles y diseño centrado en las personas."),
    Student(3, "Carlos Pérez", "Ingeniería de Software", "Tecnología", "Le interesan las aplicaciones móviles y las pruebas de software."),
    Student(4, "Ana López", "Diseño Digital", "Diseño", "Diseña interfaces accesibles y sistemas visuales."),
    Student(5, "Luis Ramírez", "Administración", "Gestión", "Desarrolla proyectos de organización y emprendimiento."),
    Student(6, "Valeria Torres", "Redes y Comunicaciones", "Tecnología", "Estudia conectividad y servicios digitales."),
    Student(7, "Diego Flores", "Gestión de Proyectos", "Gestión", "Practica planificación y trabajo colaborativo."),
    Student(8, "Camila Rojas", "Diseño de Producto", "Diseño", "Investiga necesidades de usuarios y prototipos.")
)

data class Profile(
    val name: String = "Jesus Castillo Sumire",
    val email: String = "jesus.castillo.s@tecsup.edu.pe",
    val phone: String = "",
    val bio: String = "Aprendiendo a crear experiencias Android con Kotlin y Jetpack Compose."
)

enum class DirectoryFilter(val label: String) { ALL("Todos"), TECHNOLOGY("Tecnología"), FAVORITES("Favoritos") }

/** Normaliza tildes para que 'maria' también encuentre 'María'. */
private fun String.searchKey() = Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
    .replace(Regex("\\p{M}+"), "")

fun filterStudents(source: List<Student>, query: String, filter: DirectoryFilter, favorites: Set<Int>): List<Student> {
    val key = query.searchKey()
    return source.filter { student ->
        val matches = (student.name + " " + student.career + " " + student.id).searchKey().contains(key)
        matches && when (filter) {
            DirectoryFilter.ALL -> true
            DirectoryFilter.TECHNOLOGY -> student.area == "Tecnología"
            DirectoryFilter.FAVORITES -> student.id in favorites
        }
    }
}

/** Validación de la maqueta: nunca consulta servicios ni utiliza credenciales reales. */
fun validDemoLogin(email: String, password: String) =
    email.trim().equals("demo@campus.test", ignoreCase = true) && password == "NavLab2026"

fun validateProfile(profile: Profile): String? = when {
    profile.name.trim().length !in 3..80 -> "Escribe un nombre de 3 a 80 caracteres."
    !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(profile.email.trim()) -> "Escribe un correo válido."
    profile.phone.isNotBlank() && !Regex("^\\+?[0-9 ()-]{7,20}$").matches(profile.phone.trim()) -> "Revisa el teléfono o deja el campo vacío."
    profile.bio.length > 240 -> "La biografía admite hasta 240 caracteres."
    else -> null
}

