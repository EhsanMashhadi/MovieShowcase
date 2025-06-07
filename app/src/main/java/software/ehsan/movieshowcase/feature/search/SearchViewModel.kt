package software.ehsan.movieshowcase.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.domain.SearchMovieUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) :
    ViewModel() {

    private val _uiState: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> {
                searchMovies(searchQuery.value)
            }

            is SearchIntent.BookmarkMovie -> {
                toggleBookmark(intent.movie)
            }

            is SearchIntent.UpdateSearchQuery -> {
                _searchQuery.update { intent.query }
            }
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            _uiState.update {
                SearchUiState.Loading
            }
            searchMovieUseCase(query).collect {
                it.onSuccess { movies ->
                    _uiState.update {
                        SearchUiState.Success(movies = movies)
                    }
                }
                it.onFailure { error ->
                    _uiState.update {
                        SearchUiState.Error(error.message ?: "An error occurred while searching")
                    }
                }
            }
        }
    }

    fun toggleBookmark(movie: Movie) {
        viewModelScope.launch {
            toggleBookmarkUseCase(movie)
                .onFailure { error ->
                    _uiState.update {
                        SearchUiState.Error(
                            error.message ?: "An error occurred while toggling bookmark"
                        )
                    }
                }
        }
    }


    sealed interface SearchUiState {
        data object Idle : SearchUiState
        data object Loading : SearchUiState
        data class Success(val movies: Movies) : SearchUiState
        data class Error(val error: String) : SearchUiState
    }

    sealed interface SearchIntent {
        data class UpdateSearchQuery(val query: String) : SearchIntent
        data object Search : SearchIntent
        data class BookmarkMovie(val movie: Movie) : SearchIntent
    }
}