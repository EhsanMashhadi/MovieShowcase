package software.ehsan.movieshowcase.domain

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.PagedMovies
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val getMoviesWithBookmarkStatusUseCase: GetMoviesWithBookmarkStatusUseCase
) {

    operator fun invoke(query: String): Flow<PagedMovies> {
        if (query.isBlank()) {
            return flowOf(PagedMovies(flowOf(PagingData.empty()), 0))
        }
        val pagedMoviesFlow: Flow<PagingData<Movie>> =
            movieRepository.search(query).cachedIn(movieRepository.repositoryScope)
        val pagedMoviesFlowWithBookmarkState: Flow<PagingData<Movie>> =
            getMoviesWithBookmarkStatusUseCase.perform(pagedMoviesFlow)
        val totalItemCountFlow: Flow<Int> = movieRepository.totalMoviesResultCount

        return combine(
            pagedMoviesFlowWithBookmarkState,
            totalItemCountFlow
        ) { pagedMovies, totalCount ->
            PagedMovies(
                movies = pagedMoviesFlowWithBookmarkState,
                totalResult = totalCount
            )
        }.distinctUntilChanged()
    }
}