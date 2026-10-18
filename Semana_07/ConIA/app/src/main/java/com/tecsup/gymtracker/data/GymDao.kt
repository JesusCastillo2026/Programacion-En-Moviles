package com.tecsup.gymtracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // Ignorar conflictos permite informar al formulario cuando el usuario o correo ya existe.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM Usuario WHERE nombre_usuario = :nombreUsuario AND password = :password LIMIT 1")
    suspend fun buscarPorCredenciales(nombreUsuario: String, password: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE nombre_usuario = :nombreUsuario OR email = :email LIMIT 1")
    suspend fun buscarExistente(nombreUsuario: String, email: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE id = :id LIMIT 1")
    fun buscarPorId(id: Int): Flow<Usuario?>
}

@Dao
interface RutinaDao {
    // La lista es un Flow para que Compose observe inmediatamente los cambios CRUD.
    @Insert
    suspend fun insertar(rutina: Rutina): Long

    @Query("SELECT * FROM Rutina WHERE usuario_id = :usuarioId ORDER BY fecha DESC, id DESC")
    fun listarPorUsuario(usuarioId: Int): Flow<List<Rutina>>

    @Query("SELECT * FROM Rutina WHERE id = :rutinaId LIMIT 1")
    suspend fun buscarPorId(rutinaId: Int): Rutina?

    @Update
    suspend fun actualizar(rutina: Rutina)

    @Delete
    suspend fun eliminar(rutina: Rutina)

    @Query("SELECT COUNT(*) FROM Rutina WHERE usuario_id = :usuarioId")
    fun contarPorUsuario(usuarioId: Int): Flow<Int>

    @Query("SELECT COALESCE(SUM(peso_kg * series * repeticiones), 0) FROM Rutina WHERE usuario_id = :usuarioId")
    fun volumenPorUsuario(usuarioId: Int): Flow<Double>
}
