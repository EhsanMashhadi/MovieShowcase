package software.ehsan.movieshowcase.feature.latest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.domain.GetGenresUseCase
import software.ehsan.movieshowcase.domain.GetLatestMoviesUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import software.ehsan.movieshowcase.feature.search.SearchViewModel.SearchEvent
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class LatestViewModel @Inject constructor(
    private val getLatestMoviesUseCase: GetLatestMoviesUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    @ApplicationContext private val context: Context
) :
    ViewModel() {

    private val _uiState: MutableStateFlow<LatestScreenUiState> =
        MutableStateFlow(LatestScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SearchEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val allGenre = Genre(id = 0, name = context.getString(R.string.all_allGenres))

    init {
        handleIntent(LatestIntent.LoadGenres)
        handleIntent(LatestIntent.LoadLatest(allGenre))
    }

    fun handleIntent(intent: LatestIntent) {
        when (intent) {
            is LatestIntent.LoadGenres -> {
                loadGenres()
            }

            is LatestIntent.LoadLatest -> {
                loadLatestMovies(genre = intent.selectedGenre)
            }

            is LatestIntent.BookmarkMovie -> {
                toggleBookmark(intent.movie)
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(genres = UiState.Loading)
            }
            val genresFromApi = getGenresUseCase().getOrNull().orEmpty()
            val allGenres = if (genresFromApi.isNotEmpty()) {
                listOf(allGenre) + genresFromApi
            } else {
                emptyList()
            }
            _uiState.update {
                it.copy(genres = UiState.Success(allGenres))
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

    private fun loadLatestMovies(genre: Genre?) {
        if (uiState.value.selectedGenre == genre) {
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(movies = UiState.Loading)
            }
            try {
                val genreForUseCase = if (genre?.id == 0) null else genre
                getLatestMoviesUseCase(genre = genreForUseCase).collect { pagedMovies ->
                    _uiState.update {
                        it.copy(
                            movies = UiState.Success(
                                LatestMoviesUiState(
                                    movies = pagedMovies.movies,
                                )
                            ),
                            totalResultCount = pagedMovies.totalResultCount,
                            selectedGenre = genre
                        )
                    }
                }
            } catch (_: CancellationException) {

            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(movies = UiState.Error(message = exception.message ?: "Unknown error"))
                }
            }
        }

    }

    data class LatestScreenUiState(
        val selectedGenre: Genre? = null,
        val totalResultCount: Int = 0,
        val genres: UiState<List<Genre>> = UiState.Idle,
        val movies: UiState<LatestMoviesUiState> = UiState.Idle
    )

    data class LatestMoviesUiState(
        val movies: Flow<PagingData<Movie>>,
    )

    sealed class UiState<out T> {
        data object Idle : UiState<Nothing>()
        data object Loading : UiState<Nothing>()
        data class Success<out T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }

    sealed class LatestIntent {
        data object LoadGenres : LatestIntent()
        data class LoadLatest(val selectedGenre: Genre? = null) : LatestIntent()
        data class BookmarkMovie(val movie: Movie) : LatestIntent()
    }
}