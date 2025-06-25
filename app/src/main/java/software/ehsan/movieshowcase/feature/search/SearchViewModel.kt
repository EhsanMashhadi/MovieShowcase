package software.ehsan.movieshowcase.feature.search

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.PagedMovies
import software.ehsan.movieshowcase.domain.SearchMovieUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SearchEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    @OptIn(FlowPreview::class)
    val uiState: StateFlow<SearchUiState> = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(SearchUiState.Idle)
            } else {
                searchMovieUseCase(query)
                    .map<PagedMovies, SearchUiState> { searchResult ->
                        SearchUiState.Success(
                            movies = searchResult.movies,
                            totalResult = searchResult.totalResultCount
                        )
                    }
                    .onStart { emit(SearchUiState.Loading) }
                    .catch { throwable ->
                        emit(SearchUiState.Error(throwable.localizedMessage ?: "Error"))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SearchUiState.Idle
        )

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.BookmarkMovie -> {
                toggleBookmark(intent.movie)
            }

            is SearchIntent.Search -> {
                _searchQuery.update { intent.query }
            }
        }
    }

    fun toggleBookmark(movie: Movie) {
        viewModelScope.launch {
            toggleBookmarkUseCase(movie)
                .onFailure { error ->
                    _uiEvent.emit(
                        SearchEvent.ShowToast(messageResId = R.string.all_failed_bookmark)
                    )
                }
        }
    }


    sealed interface SearchUiState {
        data object Idle : SearchUiState
        data object Loading : SearchUiState
        data class Success(val movies: Flow<PagingData<Movie>>, val totalResult: Int) :
            SearchUiState

        data class Error(val error: String) : SearchUiState
    }

    sealed interface SearchIntent {
        data class Search(val query: String) : SearchIntent
        data class BookmarkMovie(val movie: Movie) : SearchIntent
    }

    sealed interface SearchEvent {
        data class ShowToast(@StringRes val messageResId: Int) : SearchEvent
    }
}