package software.ehsan.movieshowcase.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.domain.GetMovieDetailUseCase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val getMovieDetailUseCase: GetMovieDetailUseCase) :
    ViewModel() {

    private val _uiState = MutableStateFlow<DetailState>(DetailState.Idle)
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadDetail -> {
                getMovieDetail(intent.movieId)
            }
        }
    }

    private fun getMovieDetail(movieId: Int) {
        _uiState.value = DetailState.Loading
        viewModelScope.launch {
            val movieResult = getMovieDetailUseCase(movieId)
            when {
                movieResult.isSuccess -> {
                    _uiState.value = DetailState.Success(movie = movieResult.getOrThrow())
                }

                movieResult.isFailure -> {
                    _uiState.value = DetailState.Error(
                        movieResult.exceptionOrNull()?.localizedMessage ?: "Unknown Error"
                    )
                }

            }
        }
    }
}

sealed interface DetailState {
    data object Idle : DetailState
    data object Loading : DetailState
    data class Success(val movie: Movie) : DetailState
    data class Error(val error: String) : DetailState
}

sealed interface DetailIntent {
    data class LoadDetail(val movieId: Int) : DetailIntent
}