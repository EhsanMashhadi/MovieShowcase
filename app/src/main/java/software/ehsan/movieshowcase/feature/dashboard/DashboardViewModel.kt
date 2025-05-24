package software.ehsan.movieshowcase.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.domain.GetLatestMoviesUseCase
import software.ehsan.movieshowcase.domain.GetTopMoviesUseCase
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTopMoviesUseCase: GetTopMoviesUseCase,
    private val getLatestMovieUseCase: GetLatestMoviesUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<DashboardState> = MutableStateFlow(DashboardState.Idle)
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadAllMovies -> {
                _uiState.value = DashboardState.Loading
                viewModelScope.launch {
                    val topMoviesDeferred = async { getTopMoviesUseCase() }
                    val latestMovieDeferred = async { getLatestMovieUseCase() }
                    val topMoviesResult = topMoviesDeferred.await()
                    val latestMovieResult = latestMovieDeferred.await()
                    when {
                        topMoviesResult.isFailure && latestMovieResult.isFailure -> {
                            val message = when (topMoviesResult.exceptionOrNull()) {
                                is IOException -> "No Internet Connection"
                                else -> topMoviesResult.exceptionOrNull()?.localizedMessage
                            }
                            _uiState.value = DashboardState.Error(message)
                        }

                        topMoviesResult.isSuccess || latestMovieResult.isSuccess -> {
                            val topMovies = topMoviesResult.getOrNull()
                            val latestMovie = latestMovieResult.getOrNull()
                            _uiState.value = DashboardState.Success(
                                topMovies = topMovies,
                                latestMovie = latestMovie?.results?.firstOrNull()
                            )
                        }
                    }
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
}