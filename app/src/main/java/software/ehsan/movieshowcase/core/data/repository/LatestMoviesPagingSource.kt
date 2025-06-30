package software.ehsan.movieshowcase.core.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.MutableStateFlow
import software.ehsan.movieshowcase.core.model.Genre
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.network.mapper.asDomain
import software.ehsan.movieshowcase.core.network.service.ApiException
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService

class LatestMoviesPagingSource(
    private val moviesApiService: MovieApiService,
    private val genreRepository: GenreRepository,
    private val totalItemCountFlow: MutableStateFlow<Int>,
    private val genre: Genre?,
    private val releaseDateLte: String?
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        try {
            val currentPage = params.key ?: 1
            val loadSize = params.loadSize
            println("Loading page $currentPage with size $loadSize")
            val latestMoviesResponse = moviesApiService.getLatestMovies(
                releaseDateLte = releaseDateLte,
                genreId = genre?.id,
                page = currentPage
            )
            if (!latestMoviesResponse.isSuccessful) {
                val errorBody =
                    latestMoviesResponse.errorBody()?.string() ?: ApiException.UNKNOWN_ERROR
                return LoadResult.Error<Int, Movie>(Exception(errorBody))
            }
            if (latestMoviesResponse.body() == null) {
                return LoadResult.Error<Int, Movie>(
                    ApiException.EmptyBodyException("Received successful status ${latestMoviesResponse.code()} but response body was null")
                )
            }

            val moviesResponse = latestMoviesResponse.body()!!
            if (currentPage == 1) {
                totalItemCountFlow.value = moviesResponse.totalResults
            }

            val moviesList = moviesResponse.asDomain(genreRepository = genreRepository).results
            return LoadResult.Page(
                data = moviesList,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (moviesList.isEmpty() || currentPage >= moviesResponse.totalPages) null else currentPage + 1
            )

        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}