package com.tecsup.gymtracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Usuario", indices = [Index(value = ["nombre_usuario"], unique = true), Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre_usuario") val nombreUsuario: String,
    val password: String,
    @ColumnInfo(name = "nombre_completo") val nombreCompleto: String,
    val email: String,
    val edad: Int,
    @ColumnInfo(name = "fecha_registro") val fechaRegistro: String
)
