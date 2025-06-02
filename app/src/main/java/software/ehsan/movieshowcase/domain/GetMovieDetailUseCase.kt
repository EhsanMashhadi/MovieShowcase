package software.ehsan.movieshowcase.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movie
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<Result<Movie>> {
        val moviesFlowResult = flow {
            emit(movieRepository.getMovieDetails(movieId = movieId))
        }.catch { exception ->
            emit(Result.failure(exception))
        }
        val bookmarkedMovies = movieRepository.getAllBookmarkedMovies()
            .catch { exception -> emit(Result.failure(exception)) }

        return combine(moviesFlowResult, bookmarkedMovies) { movieResult, bookmarkedMoviesResult ->

            movieResult.onFailure {
                return@combine Result.failure(it)
            }
            val movie = movieResult.getOrThrow()
            val bookmarkedIds =
                bookmarkedMoviesResult.getOrNull()?.map { it.id }?.toSet() ?: emptySet()
            val isBookmarked = bookmarkedIds.contains(movie.id)
            Result.success(movie.copy(isBookmarked = isBookmarked))
        }.distinctUntilChanged()
    }
}