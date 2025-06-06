package software.ehsan.movieshowcase.feature.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(movieRepository: MovieRepository) : ViewModel() {

}