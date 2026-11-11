package com.tecsup.lab09.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(post: FavoritePostEntity)

    @Query("SELECT * FROM favorite_posts ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<FavoritePostEntity>>

    @Query("DELETE FROM favorite_posts WHERE id = :postId")
    suspend fun deleteById(postId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_posts WHERE id = :postId)")
    suspend fun contains(postId: Int): Boolean
}
