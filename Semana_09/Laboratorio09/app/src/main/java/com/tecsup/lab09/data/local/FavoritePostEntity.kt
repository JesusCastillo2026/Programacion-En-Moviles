package com.tecsup.lab09.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tecsup.lab09.data.model.PostModel

@Entity(tableName = "favorite_posts")
data class FavoritePostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val savedAt: Long = System.currentTimeMillis()
)

fun PostModel.toFavoriteEntity() = FavoritePostEntity(
    id = id,
    userId = userId,
    title = title,
    body = body
)

fun FavoritePostEntity.toPostModel() = PostModel(
    userId = userId,
    id = id,
    title = title,
    body = body
)
