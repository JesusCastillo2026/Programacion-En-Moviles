package com.tecsup.lab09.data.repository

import com.tecsup.lab09.data.local.FavoritePostDao
import com.tecsup.lab09.data.local.FavoritePostEntity
import com.tecsup.lab09.data.local.toFavoriteEntity
import com.tecsup.lab09.data.model.PostModel
import com.tecsup.lab09.data.model.TodoModel
import com.tecsup.lab09.data.model.AmphibianModel
import com.tecsup.lab09.data.remote.AmphibianApiService
import com.tecsup.lab09.data.remote.PostApiService
import kotlinx.coroutines.flow.Flow

class PostRepository(
    private val api: PostApiService,
    private val favoritesDao: FavoritePostDao,
    private val amphibianApi: AmphibianApiService? = null
) {
    suspend fun getPosts(): List<PostModel> = api.getPosts()

    suspend fun getPost(id: Int): PostModel = api.getPostById(id)

    suspend fun getTodos(): List<TodoModel> = api.getTodos()

    suspend fun getAmphibians(): List<AmphibianModel> = requireNotNull(amphibianApi).getAmphibians()

    fun observeFavorites(): Flow<List<FavoritePostEntity>> = favoritesDao.observeAll()

    suspend fun saveFavorite(post: PostModel) = favoritesDao.save(post.toFavoriteEntity())

    suspend fun removeFavorite(postId: Int) = favoritesDao.deleteById(postId)

    suspend fun isFavorite(postId: Int): Boolean = favoritesDao.contains(postId)
}
