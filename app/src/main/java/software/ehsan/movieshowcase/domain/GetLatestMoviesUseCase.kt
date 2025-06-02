package software.ehsan.movieshowcase.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movies
import software.ehsan.movieshowcase.core.util.getCurrentDateFormatted
import javax.inject.Inject

class GetLatestMoviesUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(genre: Genre? = null, num: Int? = null): Flow<Result<Movies>> {
        val latestMovieFlow = flow {
            emit(
                movieRepository.getLatestMovies(
                    genre = genre,
                    releaseDateLte = getCurrentDateFormatted()
                )
            )
        }.catch {
            emit(Result.failure(it))
        }
        val bookmarkedMoviesFlow = movieRepository.getAllBookmarkedMovies().catch {
            emit(Result.failure(it))
        }
        return combine(
            latestMovieFlow,
            bookmarkedMoviesFlow
        ) { latestMoviesResult, bookmarkedMoviesResult ->
            latestMoviesResult.onFailure {
                return@combine Result.failure(it)
            }
            val latestMovies = latestMoviesResult.getOrNull() ?: Movies(0, emptyList(), 0, 0)
            val limitedMovies = if (num != null && num > 0) {
                latestMovies.results.take(num)
            } else {
                latestMovies.results
            }
            val bookmarkedMovieIds =
                bookmarkedMoviesResult.getOrNull()?.map { it.id }?.toSet() ?: emptySet()
            val moviesWithBookmark = limitedMovies.map { movie ->
                movie.copy(isBookmarked = bookmarkedMovieIds.contains(movie.id))
            }
            Result.success(
                Movies(
                    latestMovies.page,
                    moviesWithBookmark,
                    latestMovies.totalPages,
                    latestMovies.totalResults
                )
            )
        }.distinctUntilChanged()
    }
}
