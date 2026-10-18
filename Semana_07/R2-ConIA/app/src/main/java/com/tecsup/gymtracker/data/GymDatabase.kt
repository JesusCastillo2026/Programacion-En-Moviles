package com.tecsup.gymtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Usuario::class, Rutina::class], version = 1, exportSchema = false)
abstract class GymDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun rutinaDao(): RutinaDao
}
