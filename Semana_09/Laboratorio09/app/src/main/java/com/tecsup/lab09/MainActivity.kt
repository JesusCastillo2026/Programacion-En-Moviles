package com.tecsup.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.lab09.data.local.AppDatabase
import com.tecsup.lab09.data.remote.ApiClient
import com.tecsup.lab09.data.repository.PostRepository
import com.tecsup.lab09.ui.navigation.AppNavigation
import com.tecsup.lab09.ui.theme.Lab09Theme
import com.tecsup.lab09.viewmodel.PostViewModel
import com.tecsup.lab09.viewmodel.PostViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getInstance(applicationContext)
        val repository = PostRepository(ApiClient.service, database.favoritePostDao())

        setContent {
            val viewModel: PostViewModel = viewModel(factory = PostViewModelFactory(repository))
            Lab09Theme {
                AppNavigation(viewModel)
            }
        }
    }
}
