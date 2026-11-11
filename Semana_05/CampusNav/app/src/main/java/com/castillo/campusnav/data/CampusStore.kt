package com.castillo.campusnav.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Persistencia local del laboratorio. No almacena contraseñas ni sesiones.
 * SharedPreferences guarda datos pequeños; un servicio real requeriría otra arquitectura.
 */
class CampusStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("campus_nav", Context.MODE_PRIVATE)
    var profile by mutableStateOf(Profile(
        preferences.getString("name", null) ?: Profile().name,
        preferences.getString("email", null) ?: Profile().email,
        preferences.getString("phone", null) ?: "",
        preferences.getString("bio", null) ?: Profile().bio
    ))
        private set
    var favorites by mutableStateOf(preferences.getStringSet("favorites", emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet())
        private set

    fun toggleFavorite(id: Int) {
        if (students.none { it.id == id }) return
        favorites = if (id in favorites) favorites - id else favorites + id
        preferences.edit().putStringSet("favorites", favorites.map { it.toString() }.toSet()).apply()
    }

    fun saveProfile(value: Profile) {
        require(validateProfile(value) == null)
        profile = value.copy(name = value.name.trim(), email = value.email.trim(), phone = value.phone.trim(), bio = value.bio.trim())
        preferences.edit().putString("name", profile.name).putString("email", profile.email)
            .putString("phone", profile.phone).putString("bio", profile.bio).apply()
    }

    /** El registro propio refleja inmediatamente la edición del perfil. */
    fun directory() = students.map { if (it.id == 1) it.copy(name = profile.name, bio = profile.bio) else it }
}

