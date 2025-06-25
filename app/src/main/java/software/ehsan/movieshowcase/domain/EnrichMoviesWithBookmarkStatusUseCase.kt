package software.ehsan.movieshowcase.domain

import androidx.paging.PagingData
import androidx.paging.map
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movie

class EnrichMoviesWithBookmarkStatusUseCase @Inject constructor(movieRepository: MovieRepository) {

    private val bookmarkedMovieIdsFlow: Flow<Set<Int>> = movieRepository.getAllBookmarkedMovies()
        .map { result ->
            result.getOrNull()?.map { it.id }?.toSet() ?: emptySet()
        }
        .distinctUntilChanged()

    fun perform(moviesPagingDataFlow: Flow<PagingData<Movie>>): Flow<PagingData<Movie>> {
        return moviesPagingDataFlow.combine(bookmarkedMovieIdsFlow) { pagingData, bookmarkedIds ->
            pagingData.map { movie ->
                movie.copy(isBookmarked = bookmarkedIds.contains(movie.id))
            }
        }
    }
}