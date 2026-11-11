package com.tecsup.huellitasvet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tecsup.huellitasvet.data.local.dao.CitaDao
import com.tecsup.huellitasvet.data.local.dao.MascotaDao
import com.tecsup.huellitasvet.data.local.dao.ServicioDao
import com.tecsup.huellitasvet.data.local.dao.UsuarioDao
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.CitaServicio
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.entity.Usuario

@Database(entities = [Usuario::class, Servicio::class, Mascota::class, Cita::class, CitaServicio::class], version = 5, exportSchema = false)
abstract class VetDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun servicioDao(): ServicioDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun citaDao(): CitaDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN telefono TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE usuarios ADD COLUMN distrito TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE servicios ADD COLUMN categorias TEXT NOT NULL DEFAULT 'Consulta general'")
                db.execSQL(
                    "INSERT INTO usuarios(nombre, correo, clave, rol, telefono, distrito) " +
                        "SELECT 'Dra. Camila Rojas', 'camila.rojas@huellitas.test', 'VetHuellitas2026!', 'ADMIN', '', '' " +
                        "WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'camila.rojas@huellitas.test')"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE usuarios SET rol = 'VETERINARIO' WHERE correo = 'vet@huellitas.test'")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS cita_servicios (citaId INTEGER NOT NULL, servicioId INTEGER NOT NULL, PRIMARY KEY(citaId, servicioId), FOREIGN KEY(citaId) REFERENCES citas(id) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(servicioId) REFERENCES servicios(id) ON UPDATE NO ACTION ON DELETE NO ACTION)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cita_servicios_servicioId ON cita_servicios(servicioId)")
                db.execSQL("INSERT INTO cita_servicios(citaId, servicioId) SELECT id, servicioId FROM citas")
            }
        }

        @Volatile private var instancia: VetDatabase? = null

        fun obtener(context: Context): VetDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext,
                VetDatabase::class.java,
                "huellitasvet.db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build().also { instancia = it }
        }
    }
}
