package software.ehsan.movieshowcase.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movies
import javax.inject.Inject

private const val NUMBER_OF_RANDOM_PAGES = 10
private const val DEFAULT_PAGE_FOR_RANDOM_MOVIES = -1
private const val DEFAULT_TOTAL_PAGES_FOR_RANDOM_MOVIES = 1

class GetRandomTopMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val enrichMoviesWithBookmarkStatusUseCase: EnrichMoviesWithBookmarkStatusUseCase
) {
    suspend operator fun invoke(count: Int = 20): Flow<Result<Movies>> {
        val initialMoviesResponse = movieRepository.getTopMovies(page = 1)
        val totalPages = initialMoviesResponse.getOrElse { throwable ->
            return flow { emit(Result.failure(throwable)) }
        }.totalPages

        val randomPagesSet = mutableSetOf<Int>()
        while (randomPagesSet.size < NUMBER_OF_RANDOM_PAGES) {
            val randomNumber = (1..totalPages).random()
            randomPagesSet.add(randomNumber)
        }
        val moviesDeferred = supervisorScope {
            randomPagesSet.map { page ->
                async {
                    movieRepository.getTopMovies(page = page).onSuccess {
                        return@async it.results
                    }
                    emptyList()
                }
            }
        }
        val topMoviesFlow =
            flowOf(
                moviesDeferred.awaitAll().flatten().shuffled().sortedByDescending { it.voteAverage }
                    .take(count))
        val enrichedMovies = enrichMoviesWithBookmarkStatusUseCase.enrichMovieList(topMoviesFlow)
            .distinctUntilChanged()
        return enrichedMovies.map { enrichedMovieList ->
            Result.success(
                Movies(
                    page = DEFAULT_PAGE_FOR_RANDOM_MOVIES,
                    totalPages = DEFAULT_TOTAL_PAGES_FOR_RANDOM_MOVIES,
                    totalResultsCount = enrichedMovieList.size,
                    results = enrichedMovieList
                )
            )
        }
    }
}