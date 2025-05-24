package software.ehsan.movieshowcase.feature.latest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.domain.GetGenresUseCase
import software.ehsan.movieshowcase.domain.GetLatestMoviesUseCase
import javax.inject.Inject

@HiltViewModel
class LatestViewModel @Inject constructor(
    val getLatestMoviesUseCase: GetLatestMoviesUseCase,
    val getGenresUseCase: GetGenresUseCase,
    @ApplicationContext private val context: Context
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<LatestScreenUiState>(LatestScreenUiState())
    val uiState = _uiState.asStateFlow()

    val allGenres = Genre(id = 0, name = context.getString(R.string.all_allGenres))

    init {
        handleIntent(LatestIntent.LoadGenres)
        handleIntent(LatestIntent.LoadLatest(allGenres))
    }

    fun handleIntent(intent: LatestIntent) {
        when (intent) {
            is LatestIntent.LoadGenres -> {
                loadGenres()
            }

            is LatestIntent.LoadLatest -> {
                loadLatestMovies(genre = intent.selectedGenre)
            }
        }
    }

    private fun loadGenres() {
        _uiState.update {
            it.copy(genresState = UiState.Loading)
        }
        viewModelScope.launch {
            val genres = mutableListOf<Genre>()
            val genresFromApi = getGenresUseCase().getOrNull() ?: emptyList()
            if (genresFromApi.isNotEmpty()) {
                genres.add(allGenres)
            }
            genres.addAll(genresFromApi)
            _uiState.update {
                it.copy(UiState.Success(genres))
            }
        }
    }

    private fun loadLatestMovies(genre: Genre?) {
        if (uiState.value.selectedGenre == genre) {
            return
        }
        _uiState.update {
            it.copy(moviesState = UiState.Loading)
        }
        viewModelScope.launch {
            val latestMovies = getLatestMoviesUseCase(genre = if (genre?.id == 0) null else genre)
            if (latestMovies.isSuccess) {
                val movies = latestMovies.getOrThrow()
                _uiState.update {
                    it.copy(
                        moviesState = UiState.Success(movies),
                        selectedGenre = genre
                    )
                }
            } else {
                val message = latestMovies.exceptionOrNull()?.localizedMessage ?: "Unknown Error"
                _uiState.update {
                    it.copy(
                        moviesState = UiState.Error(message)
                    )
                }
            }
        }
    }

    data class LatestScreenUiState(
        val genresState: UiState<List<Genre>> = UiState.Loading,
        val moviesState: UiState<Movies> = UiState.Loading,
        val selectedGenre: Genre? = null
    )

    sealed class UiState<out T> {
        data object Loading : UiState<Nothing>()
        data class Success<out T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }

    sealed class LatestIntent {
        data object LoadGenres : LatestIntent()
        data class LoadLatest(val selectedGenre: Genre? = null) : LatestIntent()
    }
}