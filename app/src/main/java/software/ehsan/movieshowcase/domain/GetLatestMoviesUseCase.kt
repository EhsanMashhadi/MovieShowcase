package software.ehsan.movieshowcase.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import software.ehsan.movieshowcase.core.data.repository.MovieRepository
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.model.PagedMovies
import software.ehsan.movieshowcase.core.util.getCurrentDateFormatted
import javax.inject.Inject

class GetLatestMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val enrichMoviesWithBookmarkStatusUseCase: EnrichMoviesWithBookmarkStatusUseCase
) {

    operator fun invoke(genre: Genre? = null): Flow<PagedMovies> {
        val pagedMoviesFlow: Flow<PagingData<Movie>> = movieRepository.getLatestMovies(
            genre = genre,
            releaseDateLte = getCurrentDateFormatted()
        )
        val pagedMoviesFlowWithBookmarkState: Flow<PagingData<Movie>> =
            enrichMoviesWithBookmarkStatusUseCase.enrichPagingMovies(pagedMoviesFlow)
        val totalItemCountFlow: Flow<Int> = movieRepository.totalMoviesResultCount

        return combine(
            pagedMoviesFlowWithBookmarkState,
            totalItemCountFlow
        ) { pagedMovies, totalCount ->
            PagedMovies(
                movies = pagedMoviesFlowWithBookmarkState,
                totalResultCount = totalCount
            )
        }.distinctUntilChanged()
    }
}
