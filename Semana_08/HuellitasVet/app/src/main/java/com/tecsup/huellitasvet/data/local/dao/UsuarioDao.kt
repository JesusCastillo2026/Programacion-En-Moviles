package com.tecsup.huellitasvet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.tecsup.huellitasvet.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtener(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY nombre COLLATE NOCASE")
    fun observarPorRol(rol: String): Flow<List<Usuario>>

    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE correo = :correo)")
    suspend fun existeCorreo(correo: String): Boolean

    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Transaction
    suspend fun registrar(usuario: Usuario): Long {
        if (existeCorreo(usuario.correo)) return -1L
        return insertar(usuario)
    }

    @Insert
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}
