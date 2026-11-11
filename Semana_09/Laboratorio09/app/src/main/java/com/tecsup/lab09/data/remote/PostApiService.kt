package com.tecsup.lab09.data.remote

import com.tecsup.lab09.data.model.PostModel
import com.tecsup.lab09.data.model.TodoModel
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostModel>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): PostModel

    @GET("todos")
    suspend fun getTodos(): List<TodoModel>
}
