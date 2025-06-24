package software.ehsan.movieshowcase.core.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import software.ehsan.movieshowcase.core.model.Movie
import software.ehsan.movieshowcase.core.network.mapper.asDomain
import software.ehsan.movieshowcase.core.network.service.ApiException
import software.ehsan.movieshowcase.core.network.service.api.GenreApiService
import software.ehsan.movieshowcase.core.network.service.api.MovieApiService

class MoviesPagingSource(
    private val moviesApiService: MovieApiService,
    private val genreApiService: GenreApiService,
    private val query: String,
    private val totalItemCountFlow: MutableStateFlow<Int>
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        try {
            val currentPage = params.key ?: 1
            val searchResponse = moviesApiService.search(query = query, page = currentPage)
            if (!searchResponse.isSuccessful) {
                val errorBody =
                    searchResponse.errorBody()?.string() ?: ApiException.UNKNOWN_ERROR
                return LoadResult.Error<Int, Movie>(Exception(errorBody))
            }
            if (searchResponse.body() == null) {
                return LoadResult.Error<Int, Movie>(
                    ApiException.EmptyBodyException("Received successful status ${searchResponse.code()} but response body was null")
                )
            }

            val moviesResponse = searchResponse.body()!!
            if (currentPage == 1) {
                totalItemCountFlow.value = moviesResponse.totalResults
            }
            val genresMapping = getGenreMapping()
            val moviesList = moviesResponse.results.map { topMovie ->
                val genreNames = topMovie.genres?.mapNotNull { genresMapping[it] }
                val movieDomain = topMovie.asDomain(genreNames)
                movieDomain
            }
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

    suspend fun getGenreMapping(): Map<Int, String> {
        val genresResponse = genreApiService.getMoviesGenreIds()
        return if (genresResponse.isSuccessful) {
            genresResponse.body()?.let { genreResponse ->
                genreResponse.genres.associateBy({ it.id }, { it.name })
            } ?: emptyMap()
        } else emptyMap()
    }
}