package software.ehsan.movieshowcase.feature.detail

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import software.ehsan.movieshowcase.R
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.domain.GetMovieDetailUseCase
import software.ehsan.movieshowcase.domain.ToggleBookmarkUseCase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<DetailState>(DetailState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<DetailEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadDetail -> {
                getMovieDetail(intent.movieId)
            }

            is DetailIntent.ToggleBookmark -> {
                viewModelScope.launch {
                    val result = toggleBookmarkUseCase(intent.movie)
                    result.onFailure {
                        _uiEvent.emit(
                            DetailEvent.ShowToast(messageResId = R.string.app_name)
                        )
                    }
                }
            }
        }
    }

    private fun getMovieDetail(movieId: Int) {
        _uiState.value = DetailState.Loading
        viewModelScope.launch {
            getMovieDetailUseCase.invoke(movieId).collect {
                it.onSuccess {
                    _uiState.value = DetailState.Success(movie = it)
                }
                it.onFailure {
                    _uiState.value = DetailState.Error(
                        it.localizedMessage ?: "Unknown Error"
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
    data class ToggleBookmark(val movie: Movie) : DetailIntent
}

sealed interface DetailEvent {
    data class ShowToast(@StringRes val messageResId: Int) : DetailEvent
}