package com.tecsup.gymtracker.data

import android.content.Context
import androidx.room.Room
import java.time.LocalDate

object Repositorio {
    // Esta instancia única centraliza el acceso a Room para todas las pantallas.
    private lateinit var db: GymDatabase
    lateinit var usuarioDao: UsuarioDao
        private set
    lateinit var rutinaDao: RutinaDao
        private set

    fun inicializar(context: Context) {
        if (!::db.isInitialized) {
            db = Room.databaseBuilder(context, GymDatabase::class.java, "gymtracker.db").build()
            usuarioDao = db.usuarioDao()
            rutinaDao = db.rutinaDao()
        }
    }

    suspend fun registrar(usuario: Usuario): Boolean {
        // Se validan campos y duplicados antes de intentar insertar el socio.
        if (usuario.nombreCompleto.isBlank() || usuario.nombreUsuario.isBlank() ||
            !usuario.email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) ||
            usuario.edad !in 14..100 || usuario.password.length < 6 ||
            usuarioDao.buscarExistente(usuario.nombreUsuario, usuario.email) != null) return false
        return usuarioDao.insertar(usuario) > 0
    }

    suspend fun iniciarSesion(nombreUsuario: String, password: String): Usuario? =
        usuarioDao.buscarPorCredenciales(nombreUsuario.trim(), password)

    suspend fun guardarRutina(rutina: Rutina): Boolean {
        if (!rutinaValida(rutina)) return false
        rutinaDao.insertar(rutina)
        return true
    }

    suspend fun guardarRutinaValidada(rutina: Rutina): Boolean {
        if (!rutinaValida(rutina)) return false
        rutinaDao.actualizar(rutina)
        return true
    }

    private fun rutinaValida(rutina: Rutina): Boolean =
        rutina.ejercicio.isNotBlank() && rutina.grupoMuscular.isNotBlank() && rutina.series in 1..20 &&
            rutina.repeticiones in 1..100 && rutina.pesoKg in 0.0..1000.0 &&
            runCatching { LocalDate.parse(rutina.fecha) }.isSuccess

    fun cerrarBaseDatos() {
        if (::db.isInitialized) db.close()
    }
}
