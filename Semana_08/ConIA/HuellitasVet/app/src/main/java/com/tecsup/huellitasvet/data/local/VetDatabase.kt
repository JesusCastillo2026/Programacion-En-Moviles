package com.tecsup.huellitasvet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.huellitasvet.data.local.dao.CitaDao
import com.tecsup.huellitasvet.data.local.dao.MascotaDao
import com.tecsup.huellitasvet.data.local.dao.ServicioDao
import com.tecsup.huellitasvet.data.local.dao.UsuarioDao
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.entity.Usuario

@Database(entities = [Usuario::class, Servicio::class, Mascota::class, Cita::class], version = 1, exportSchema = false)
abstract class VetDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun servicioDao(): ServicioDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile private var instancia: VetDatabase? = null

        fun obtener(context: Context): VetDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext,
                VetDatabase::class.java,
                "huellitasvet.db"
            ).build().also { instancia = it }
        }
    }
}
