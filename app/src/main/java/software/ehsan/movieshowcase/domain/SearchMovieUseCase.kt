package software.ehsan.movieshowcase.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movies
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(query: String): Flow<Result<Movies>> {
        if (query.isBlank()) {
            return flowOf(
                Result.success(
                    Movies(
                        page = 1,
                        results = emptyList(),
                        totalPages = 1,
                        totalResults = 0
                    )
                )
            )
        }
        val moviesResultFlow = flow {
            emit(movieRepository.search(query))
        }.catch { exception ->
            emit(Result.failure(exception))
        }
        val bookmarkedMoviesResultFlow = movieRepository.getAllBookmarkedMovies().catch {
            emit(Result.failure(it))
        }
        return combine(
            moviesResultFlow,
            bookmarkedMoviesResultFlow
        ) { moviesResult, bookmarkedMoviesResult ->
            moviesResult.onFailure {
                return@combine Result.failure(it)
            }
            val movies = moviesResult.getOrNull() ?: Movies(0, emptyList(), 0, 0)
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
                    movies.totalResults
                )
            )
        }.distinctUntilChanged()
    }
}