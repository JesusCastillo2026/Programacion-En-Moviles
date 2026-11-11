package com.tecsup.lab09.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lab09.data.local.FavoritePostEntity
import com.tecsup.lab09.data.model.PostModel
import com.tecsup.lab09.data.model.TodoModel
import com.tecsup.lab09.data.model.AmphibianModel
import com.tecsup.lab09.data.repository.PostRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PostsUiState(
    val isLoading: Boolean = false,
    val posts: List<PostModel> = emptyList(),
    val error: String? = null
)

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: PostModel? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)

data class TodosUiState(
    val isLoading: Boolean = false,
    val todos: List<TodoModel> = emptyList(),
    val error: String? = null
)

data class AmphibiansUiState(
    val isLoading: Boolean = false,
    val animals: List<AmphibianModel> = emptyList(),
    val error: String? = null
)

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    private val _posts = MutableStateFlow(PostsUiState(isLoading = true))
    val posts: StateFlow<PostsUiState> = _posts.asStateFlow()

    private val _detail = MutableStateFlow(PostDetailUiState())
    val detail: StateFlow<PostDetailUiState> = _detail.asStateFlow()

    private val _todos = MutableStateFlow(TodosUiState(isLoading = true))
    val todos: StateFlow<TodosUiState> = _todos.asStateFlow()

    private val _amphibians = MutableStateFlow(AmphibiansUiState(isLoading = true))
    val amphibians: StateFlow<AmphibiansUiState> = _amphibians.asStateFlow()

    val favorites: StateFlow<List<FavoritePostEntity>> = repository.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private var detailJob: Job? = null

    init {
        loadPosts()
        loadTodos()
        loadAmphibians()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _posts.value = _posts.value.copy(isLoading = true, error = null)
            runCatching { repository.getPosts() }
                .onSuccess { _posts.value = PostsUiState(posts = it) }
                .onFailure { _posts.value = PostsUiState(error = it.message ?: "No se pudieron cargar las publicaciones.") }
        }
    }

    fun loadPost(postId: Int) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _detail.value = PostDetailUiState(isLoading = true)
            runCatching { repository.getPost(postId) }
                .onSuccess { post ->
                    val favorite = runCatching { repository.isFavorite(postId) }.getOrDefault(false)
                    _detail.value = PostDetailUiState(post = post, isFavorite = favorite)
                }
                .onFailure { _detail.value = PostDetailUiState(error = it.message ?: "No se encontró la publicación.") }
        }
    }

    fun toggleFavorite(post: PostModel) {
        viewModelScope.launch {
            runCatching {
                if (repository.isFavorite(post.id)) repository.removeFavorite(post.id)
                else repository.saveFavorite(post)
            }.onSuccess {
                _detail.value = _detail.value.copy(isFavorite = repository.isFavorite(post.id))
            }
        }
    }

    fun removeFavorite(postId: Int) {
        viewModelScope.launch { repository.removeFavorite(postId) }
    }

    fun loadTodos() {
        viewModelScope.launch {
            _todos.value = _todos.value.copy(isLoading = true, error = null)
            runCatching { repository.getTodos() }
                .onSuccess { _todos.value = TodosUiState(todos = it) }
                .onFailure { _todos.value = TodosUiState(error = it.message ?: "No se pudieron cargar las tareas.") }
        }
    }

    fun loadAmphibians() {
        viewModelScope.launch {
            _amphibians.value = _amphibians.value.copy(isLoading = true, error = null)
            runCatching { repository.getAmphibians() }
                .onSuccess { _amphibians.value = AmphibiansUiState(animals = it) }
                .onFailure { _amphibians.value = AmphibiansUiState(error = it.message ?: "No se pudo cargar el catálogo.") }
        }
    }
}
