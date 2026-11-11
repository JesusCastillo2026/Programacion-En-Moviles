package com.tecsup.lab09.data.model

import com.google.gson.annotations.SerializedName

data class TodoModel(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("completed") val completed: Boolean
)
