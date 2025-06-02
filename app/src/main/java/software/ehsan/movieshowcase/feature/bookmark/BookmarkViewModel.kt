package software.ehsan.movieshowcase.feature.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.domain.GetBookmarkedMoviesUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarkedMoviesUseCase: GetBookmarkedMoviesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkState>(BookmarkState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(BookmarkIntent.LoadBookmarks)
    }

    fun handleIntent(intent: BookmarkIntent) {
        when (intent) {
            is BookmarkIntent.LoadBookmarks -> {
                _uiState.update { BookmarkState.Loading }
                viewModelScope.launch {
                    getBookmarkedMoviesUseCase().collect {
                        it.onFailure { error ->
                            _uiState.update {
                                BookmarkState.Error(
                                    error.message ?: "An error occurred while loading bookmarks"
                                )
                            }
                        }
                        it.onSuccess { bookmarkedMovies ->
                            _uiState.update {
                                BookmarkState.Success(bookmarkedMovies = bookmarkedMovies)
                            }
                        }
                    }
                }
            }

            is BookmarkIntent.BookmarkMovie -> {
                viewModelScope.launch {
                    toggleBookmarkUseCase(intent.movie)
                        .onFailure { error ->
                            _uiState.update {
                                BookmarkState.Error(
                                    error.message ?: "An error occurred while toggling bookmark"
                                )
                            }
                        }
                }
            }
        }
    }
}

sealed interface BookmarkIntent {
    data object LoadBookmarks : BookmarkIntent
    data class BookmarkMovie(val movie: Movie) : BookmarkIntent
}

sealed interface BookmarkState {
    data object Idle : BookmarkState
    data object Loading : BookmarkState
    data class Success(val bookmarkedMovies: List<Movie>) : BookmarkState
    data class Error(val message: String) : BookmarkState
}