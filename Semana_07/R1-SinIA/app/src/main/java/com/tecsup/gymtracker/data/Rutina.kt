package com.tecsup.gymtracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Rutina",
    foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["usuario_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("usuario_id")]
)
data class Rutina(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    val ejercicio: String,
    @ColumnInfo(name = "grupo_muscular") val grupoMuscular: String,
    val series: Int,
    val repeticiones: Int,
    @ColumnInfo(name = "peso_kg") val pesoKg: Double,
    val fecha: String
)
