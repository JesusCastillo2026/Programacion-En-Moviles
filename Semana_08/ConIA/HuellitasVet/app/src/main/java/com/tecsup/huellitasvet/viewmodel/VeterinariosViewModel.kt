package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VeterinariosViewModel(private val repo: VetRepository) : ViewModel() {
    val veterinarios: StateFlow<List<Usuario>> = repo.observarVeterinarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mensaje = MutableStateFlow<String?>(null)

    fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        distrito: String,
        clave: String,
        confirmarClave: String,
        onExito: () -> Unit
    ) {
        val email = correo.trim()
        val numero = telefono.filter(Char::isDigit)
        if (nombre.isBlank() || email.isBlank() || numero.length != 9 || distrito.isBlank()) {
            mensaje.value = "Completa nombre, correo, teléfono de 9 dígitos y distrito"
            return
        }
        if (!email.matches(Regex("^[A-Za-z0-9+._%\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$"))) {
            mensaje.value = "Escribe un correo válido"
            return
        }
        if (!clave.matches(Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S{8,}$"))) {
            mensaje.value = "La contraseña no cumple los requisitos"
            return
        }
        if (clave != confirmarClave) {
            mensaje.value = "Las contraseñas no coinciden"
            return
        }
        viewModelScope.launch {
            runCatching {
                repo.registrarUsuario(
                    Usuario(
                        nombre = nombre.trim(), correo = email, clave = clave,
                        rol = Rol.VETERINARIO.name, telefono = numero, distrito = distrito.trim()
                    )
                )
            }.onSuccess { guardado ->
                if (guardado) {
                    mensaje.value = "Cuenta veterinaria creada. Ya puede iniciar sesión."
                    onExito()
                } else {
                    mensaje.value = "Ya existe una cuenta con ese correo"
                }
            }.onFailure {
                mensaje.value = "No se pudo crear la cuenta veterinaria"
            }
        }
    }
}
