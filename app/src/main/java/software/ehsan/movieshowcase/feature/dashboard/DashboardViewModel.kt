package software.ehsan.movieshowcase.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.domain.GetLatestMoviesUseCase
import software.ehsan.movieshowcase.domain.GetTopMovieUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import software.ehsan.movieshowcase.feature.dashboard.DashboardState.Idle
import software.ehsan.movieshowcase.feature.dashboard.DashboardState.Loading
import software.ehsan.movieshowcase.feature.dashboard.DashboardState.Success
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTopMovieUseCase: GetTopMovieUseCase,
    private val getLatestMovieUseCase: GetLatestMoviesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<DashboardState> = MutableStateFlow(Idle)
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadAllMovies -> {
                _uiState.value = Loading
                viewModelScope.launch {
                    combine(
                        getTopMovieUseCase(),
                        getLatestMovieUseCase(num = 1)
                    ) { topMovies, latestMovies ->
                        when {
                            topMovies.isFailure && latestMovies.isFailure -> {
                                DashboardState.Error("failed to load movies")
                            }

                            topMovies.isSuccess || latestMovies.isSuccess -> {
                                val topMovies = topMovies.getOrNull()
                                val latestMovie = latestMovies.getOrNull()
                                Success(
                                    topMovies = topMovies,
                                    latestMovie = latestMovie?.results?.firstOrNull()
                                )
                            }

                            else -> Idle
                        }
                    }.collect {
                        _uiState.value = it
                    }
                }
            }

            is DashboardIntent.BookmarkMovie -> {
                viewModelScope.launch {
                    toggleBookmarkUseCase(intent.movie)
                }
            }
        }
    }
}

sealed interface DashboardState {
    data object Idle : DashboardState
    data object Loading : DashboardState
    data class Success(val topMovies: Movies?, val latestMovie: Movie?) : DashboardState
    data class Error(val error: String?) : DashboardState
}

sealed interface DashboardIntent {
    data object LoadAllMovies : DashboardIntent
    data class BookmarkMovie(val movie: Movie) : DashboardIntent
}