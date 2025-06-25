package software.ehsan.movieshowcase.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movies
import javax.inject.Inject

class GetTopMovieUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<Result<Movies>> {
        val topMoviesResultFlow = flow {
            emit(movieRepository.getTopMovies())
        }.catch { exception ->
            emit(Result.failure(exception))
        }
        val bookmarkedMoviesResultFlow = movieRepository.getAllBookmarkedMovies().catch {
            emit(Result.failure(it))
        }
        return combine(
            topMoviesResultFlow,
            bookmarkedMoviesResultFlow
        ) { topMoviesResult, bookmarkedMoviesResult ->
            topMoviesResult.onFailure {
                return@combine Result.failure(it)
            }
            val movies = topMoviesResult.getOrNull() ?: Movies(0, emptyList(), 0, 0)
            val bookmarkedMovieIds =
                bookmarkedMoviesResult.getOrNull()?.map { it.id }?.toSet() ?: emptySet()
            val moviesWithBookmark = movies.results.map { movie ->
                movie.copy(isBookmarked = bookmarkedMovieIds.contains(movie.id))
            }
            Result.success(
                Movies(
                    movies.page,
                    moviesWithBookmark,
                    movies.totalPages,
                    movies.totalResultsCount
                )
            )
        }.distinctUntilChanged()
    }
}
